package library.storage.akka.message;

import akka.actor.ActorRef;
import library.engine.message.EventMessage;
import library.core.eaa.Event;

/**
 * @author Alexander Kuleshov
 */
public class StorageEventMessage extends EventMessage {
    public final ActorRef origin;

    public StorageEventMessage(Event event, ActorRef origin) {
        super(event);
        this.origin = origin;
    }
}
