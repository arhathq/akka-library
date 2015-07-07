package library.storage.akka.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import library.core.akka.AkkaService;
import library.core.eaa.Activities;
import library.core.eaa.Activity;
import library.core.eaa.Event;
import library.storage.StorageException;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.eaa.StorageEventType;
import library.storage.akka.message.StorageEventMessage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Alexander Kuleshov
 */
public class StorageRouter extends UntypedActor {

    @Autowired
    private AkkaService akkaService;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof StorageEventMessage) {
            routeEvent((StorageEventMessage) message);
        } else {
            unhandled(message);
        }
    }

    private void routeEvent(StorageEventMessage message) throws Exception {
        Event event = message.event;

        ActorRef actorProcessor;
        if (StorageEventType.GET_BOOKS == event.getEventType()) {
            actorProcessor = akkaService.getSpringActor("bookActor");
        } else if (StorageEventType.SAVE_BOOK == event.getEventType()) {
            actorProcessor = akkaService.getSpringActor("bookActor");
        } else {
            Activity errorActivity = Activities.createErrorActivity(new StorageException("Unknown event: " + event.getEventType()));
            sender().tell(new StorageActivityMessage(errorActivity), self());
            return;
        }
        actorProcessor.forward(message, context());
    }
}
