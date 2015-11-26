package library.storage.akka.message;

import akka.actor.ActorRef;
import library.engine.message.EventMessage;
import library.core.eaa.Event;

/**
 * @author Alexander Kuleshov
 */
public class StorageEventMessage extends EventMessage {
    public final long id;
    public final ActorRef origin;

    public StorageEventMessage(long id, Event event, ActorRef origin) {
        super(event);
        this.id = id;
        this.origin = origin;
    }
}
