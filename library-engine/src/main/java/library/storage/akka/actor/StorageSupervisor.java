package library.storage.akka.actor;

import akka.actor.ActorRef;
import akka.actor.Status;
import akka.actor.UntypedActor;
import library.core.eaa.ThrowableActivity;
import library.engine.message.ActivityMessage;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.akka.message.StorageErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import library.core.akka.AkkaService;
import library.storage.akka.message.StorageEventMessage;
import library.engine.message.EventMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Alexander Kuleshov
 */
public class StorageSupervisor extends UntypedActor {

    private Map<Long, ActorRef> routes = new ConcurrentHashMap<>();

    private AtomicLong id = new AtomicLong(0);

    @Autowired
    private AkkaService akkaService;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof EventMessage) {
            delegateEvent((EventMessage) message);
        } else if (message instanceof StorageActivityMessage) {
            ActorRef sender = routes.remove(((StorageActivityMessage) message).id);
            sender.tell(new ActivityMessage(((StorageActivityMessage) message).activity), self());
        } else if (message instanceof StorageErrorMessage) {
            ActorRef sender = routes.remove(((StorageActivityMessage) message).id);
            handleError((StorageErrorMessage) message, sender);
        } else {
            unhandled(message);
        }
    }

    public void delegateEvent(EventMessage message) {
        ActorRef actorProcessor = akkaService.getActor("storageRouter");
        long eventId = id.incrementAndGet();
        routes.put(eventId, sender());
        actorProcessor.tell(new StorageEventMessage(eventId, message.event, self()), self());
    }

    public void handleError(StorageErrorMessage message, ActorRef sender) {
        sender.tell(new Status.Failure((message.getThrowableActivity()).getThrowable()), self());
    }
}