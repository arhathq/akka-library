package library.storage.akka.actor;

import akka.actor.ActorRef;
import akka.actor.Status;
import akka.actor.UntypedActor;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.eaa.StorageEventType;
import library.storage.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import library.core.akka.AkkaService;
import library.storage.akka.message.StorageEventMessage;
import library.engine.message.EventMessage;

/**
 * @author Alexander Kuleshov
 */
public class StorageSupervisor extends UntypedActor {

    @Autowired
    private AkkaService akkaService;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof StorageActivityMessage) {
            handleActivity((StorageActivityMessage) message);
        } if (message instanceof EventMessage) {
            delegateEvent((EventMessage) message);
        } else {
            unhandled(message);
        }
    }

    public void delegateEvent(EventMessage message) {
        ActorRef actorProcessor = akkaService.getSpringActor("storageRouter");
        actorProcessor.tell(new StorageEventMessage(message.event, sender()), self());
    }

    public void handleActivity(StorageActivityMessage message) {
//        if (message.activity.getThrowable() != null) {
//            message.origin.tell(new Status.Failure(message.activity.getThrowable()), self());
//        } else {
//            // another
//        }
    }
}
