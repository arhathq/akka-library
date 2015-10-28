package library.storage.akka.actor;

import akka.actor.ActorRef;
import akka.actor.Status;
import akka.actor.UntypedActor;
import library.storage.akka.message.StorageErrorMessage;
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
        if (message instanceof EventMessage) {
            delegateEvent((EventMessage) message);
        } else if (message instanceof StorageErrorMessage) {
            handleError((StorageErrorMessage) message);
        } else {
            unhandled(message);
        }
    }

    public void delegateEvent(EventMessage message) {
        ActorRef actorProcessor = akkaService.getActor("storageRouter");
        actorProcessor.tell(new StorageEventMessage(message.event, sender()), self());
    }

    public void handleError(StorageErrorMessage message) {
        message.destination.tell(new Status.Failure((message.getThrowableActivity()).getThrowable()), self());
    }
}