package library.storage.akka.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
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
        } else {
            unhandled(message);
        }
    }

    public void delegateEvent(EventMessage request) {
        ActorRef actorProcessor = akkaService.getSpringActor("storageRouter");
        actorProcessor.tell(new StorageEventMessage(request.event, sender()), self());
    }
}
