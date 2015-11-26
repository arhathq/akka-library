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

    private static final String BOOK_ACTOR = "bookActor";
    private static final String AUTHOR_BOOK = "authorBook";
    private static final String PUBLISHER_ACTOR = "publisherActor";

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

        if (event == null) {
            sendError(message.id, new StorageException("No event to process"), message.origin);
            return;
        }

        ActorRef actorProcessor;
        if (StorageEventType.GET_BOOKS == event.getEventType()) {
            actorProcessor = akkaService.createActor(BOOK_ACTOR);
        } else if (StorageEventType.GET_BOOK == event.getEventType()) {
            actorProcessor = akkaService.createActor(BOOK_ACTOR);
        } else if (StorageEventType.SAVE_BOOK == event.getEventType()) {
            actorProcessor = akkaService.createActor(BOOK_ACTOR);
        } else if (StorageEventType.GET_AUTHORS == event.getEventType()) {
            actorProcessor = akkaService.createActor(AUTHOR_BOOK);
        } else if (StorageEventType.GET_AUTHOR == event.getEventType()) {
            actorProcessor = akkaService.createActor(AUTHOR_BOOK);
        } else if (StorageEventType.SAVE_AUTHOR == event.getEventType()) {
            actorProcessor = akkaService.createActor(AUTHOR_BOOK);
        } else if (StorageEventType.GET_PUBLISHERS == event.getEventType()) {
            actorProcessor = akkaService.createActor(PUBLISHER_ACTOR);
        } else if (StorageEventType.GET_PUBLISHER == event.getEventType()) {
            actorProcessor = akkaService.createActor(PUBLISHER_ACTOR);
        } else if (StorageEventType.SAVE_PUBLISHER == event.getEventType()) {
            actorProcessor = akkaService.createActor(PUBLISHER_ACTOR);
        } else {
            sendError(message.id, new StorageException("Unknown event: " + event.getEventType()), message.origin);
            return;
        }
        actorProcessor.forward(message, context());
    }

    private void sendError(long id, Throwable t, ActorRef dest) {
        ThrowableActivity errorActivity = Activities.createErrorActivity(t);
        sender().tell(new StorageErrorMessage(id, errorActivity, dest), self());
    }
}