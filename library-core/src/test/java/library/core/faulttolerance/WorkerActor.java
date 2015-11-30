package library.core.faulttolerance;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.Timeout;
import library.core.akka.AkkaService;
import org.springframework.beans.factory.annotation.Autowired;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static akka.japi.Util.classTag;
import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

import static library.core.faulttolerance.CounterServiceApi.*;
import static library.core.faulttolerance.WorkerApi.*;

/**
 * @author Alexander Kuleshov
 */
public class WorkerActor extends UntypedActor {

    private LoggingAdapter logger = Logging.getLogger(context().system(), this);

    private Timeout timeout = Timeout.apply(Duration.apply(5, TimeUnit.SECONDS));

    @Autowired
    private AkkaService akkaService;

    // The sender of the initial Start message will continuously be notified about progress
    private ActorRef listenerActor;
    private ActorRef serviceActor;

    final int totalCount = 51;

    // Stop the ServiceActor child if it throws ServiceUnavailable
    private static final SupervisorStrategy strategy =
            new OneForOneStrategy(-1, Duration.Inf(), t -> {
                if (t instanceof ServiceUnavailable) {
                    return SupervisorStrategy.stop();
                } else {
                    return SupervisorStrategy.escalate();
                }
            });

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public void preStart() throws Exception {
        serviceActor = akkaService.getActor("serviceActor");
        super.preStart();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        logger.debug("Received message {}", message);
        if (message.equals(Start) && listenerActor == null) {

            listenerActor = sender();
            akkaService.schedule(context(), self(), ActorRef.noSender(), Do, Duration.Zero(), Duration.apply(1, TimeUnit.SECONDS));

        } else if (message.equals(Do)) {
            akkaService.sendMessage(new Increment(1), serviceActor, self());
            akkaService.sendMessage(new Increment(1), serviceActor, self());
            akkaService.sendMessage(new Increment(1), serviceActor, self());

            pipe(ask(serviceActor, GetCurrentCount, timeout)
                    .mapTo(classTag(CurrentCount.class))
                    .map(new Mapper<CurrentCount, Progress>() {
                        public Progress apply(CurrentCount c) {
                            return new Progress(100.0 * c.count / totalCount);
                        }
                    }, context().dispatcher()), context().dispatcher()
            ).to(listenerActor);

        } else {
            unhandled(message);
        }
    }
}