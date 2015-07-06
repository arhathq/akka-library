package library.storage.akka.actor;

import akka.actor.UntypedActor;
import library.core.akka.AkkaService;
import library.core.eaa.Event;
import library.storage.StorageEventType;
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

        if (StorageEventType.GET_BOOKS == event.getEventType()) {

        } else if (StorageEventType.SAVE_BOOK == event.getEventType()) {

        }
    }
}
