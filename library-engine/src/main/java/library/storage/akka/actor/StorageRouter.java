package library.storage.akka.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import library.core.akka.AkkaService;
import library.core.eaa.Activities;
import library.core.eaa.Event;
import library.core.eaa.ThrowableActivity;
import library.storage.StorageException;
import library.storage.akka.message.StorageErrorMessage;
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
            actorProcessor = akkaService.createActor("bookActor");
        } else if (StorageEventType.GET_BOOK == event.getEventType()) {
            actorProcessor = akkaService.createActor("bookActor");
        } else if (StorageEventType.SAVE_BOOK == event.getEventType()) {
            actorProcessor = akkaService.createActor("bookActor");
        } else if (StorageEventType.GET_AUTHORS == event.getEventType()) {
            actorProcessor = akkaService.createActor("authorActor");
        } else if (StorageEventType.GET_AUTHOR == event.getEventType()) {
            actorProcessor = akkaService.createActor("authorActor");
        } else if (StorageEventType.SAVE_AUTHOR == event.getEventType()) {
            actorProcessor = akkaService.createActor("authorActor");
        } else if (StorageEventType.GET_PUBLISHERS == event.getEventType()) {
            actorProcessor = akkaService.createActor("publisherActor");
        } else if (StorageEventType.GET_PUBLISHER == event.getEventType()) {
            actorProcessor = akkaService.createActor("publisherActor");
        } else if (StorageEventType.SAVE_PUBLISHER == event.getEventType()) {
            actorProcessor = akkaService.createActor("publisherActor");
        } else {
            ThrowableActivity errorActivity = Activities.createErrorActivity(new StorageException("Unknown event: " + event.getEventType()));
            sender().tell(new StorageErrorMessage(errorActivity, message.origin), self());
            return;
        }
        actorProcessor.forward(message, context());
    }
}