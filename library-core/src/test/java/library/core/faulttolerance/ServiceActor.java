package library.core.faulttolerance;

import akka.actor.*;
import akka.event.LoggingReceive;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import library.core.akka.AkkaService;
import org.springframework.beans.factory.annotation.Autowired;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;

import static library.core.faulttolerance.CounterApi.*;
import static library.core.faulttolerance.CounterServiceApi.*;
import static library.core.faulttolerance.StorageApi.*;

/**
 *
 * @author Alexander Kuleshov
 */
public class ServiceActor extends AbstractLoggingActor {

    // Reconnect message
    static final Object Reconnect = "Reconnect";

    private static class SenderMsgPair {
        final ActorRef sender;
        final Object msg;

        SenderMsgPair(ActorRef sender, Object msg) {
            this.msg = msg;
            this.sender = sender;
        }
    }

    final String key = self().path().name();

    ActorRef storageActor;
    ActorRef counterActor;

    final List<SenderMsgPair> backlog = new ArrayList<>();

    final int MAX_BACKLOG = 10000;

    @Autowired
    private AkkaService akkaService;

    // Restart the storage child when StorageException is thrown.
    // After 3 restarts within 5 seconds it will be stopped.
    private final SupervisorStrategy strategy =
            new OneForOneStrategy(3, Duration.create("5 seconds"),
                    DeciderBuilder.
                            match(StorageException.class, e -> SupervisorStrategy.restart()).
                            matchAny(o -> SupervisorStrategy.escalate()).build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public void preStart() throws Exception {
        initStorage();
    }

    /**
     * The child storage is restarted in case of failure, but after 3 restarts,
     * and still failing it will be stopped. Better to back-off than
     * continuously failing. When it has been stopped we will schedule a
     * Reconnect after a delay. Watch the child so we receive Terminated message
     * when it has been terminated.
     */
    void initStorage() {
        storageActor = context().watch(akkaService.createActor(context(), "storageActor", "storageActor"));

        // Tell the counter, if any, to use the new storage
        if (counterActor != null) {
            akkaService.sendMessage(new UseStorage(storageActor), counterActor, self());
        }

        // We need the initial value to be able to operate
        akkaService.sendMessage(new Get(key), storageActor, self());
    }

    public ServiceActor() {
        receive(LoggingReceive.create(
                ReceiveBuilder.match(Entry.class,
                        entry -> entry.key.equals(key) && counterActor == null,
                        entry -> {
                            // Reply from Storage of the initial value, now we can create the Counter
                            final long value = entry.value;
                            counterActor = context().actorOf(Props.create(CounterActor.class, key, value));

                            // Tell the counter to use current storage
                            akkaService.sendMessage(new UseStorage(storageActor), counterActor, self());

                            // and send the buffered backlog to the counter
                            for (SenderMsgPair each : backlog) {
                                akkaService.sendMessage(each.msg, counterActor, each.sender);
                            }
                            backlog.clear();
                        }).
                        match(Increment.class, increment -> {
                            forwardOrPlaceInBacklog(increment);
                        }).
                        matchEquals(GetCurrentCount, gcc -> {
                            forwardOrPlaceInBacklog(gcc);
                        }).
                        match(Terminated.class, o -> {
                            // After 3 restarts the storage child is stopped.
                            // We receive Terminated because we watch the child, see initStorage.
                            storageActor = null;

                            // Tell the counter that there is no storage for the moment
                            akkaService.sendMessage(new UseStorage(null), counterActor, self());

                            // Try to re-establish storage after while
                            akkaService.scheduleOnce(context(), self(), ActorRef.noSender(), Reconnect, Duration.create(10, "seconds"));
                        }).
                        matchEquals(Reconnect, o -> {
                            // Re-establish storage after the scheduled delay
                            initStorage();
                        }).build(), context())
        );
    }

    void forwardOrPlaceInBacklog(Object msg) {
        // We need the initial value from storage before we can start delegate to
        // the counter. Before that we place the messages in a backlog, to be sent
        // to the counter when it is initialized.
        if (counterActor == null) {
            if (backlog.size() >= MAX_BACKLOG) {
                throw new ServiceUnavailable("CounterService not available, lack of initial value");
            }
            backlog.add(new SenderMsgPair(sender(), msg));
        } else {
            counterActor.forward(msg, context());
        }
    }
}