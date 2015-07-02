package library.book.akka.message;

import akka.actor.ActorRef;
import library.core.eaa.EventMessage;
import library.core.eaa.Event;

/**
 * @author Alexander Kuleshov
 */
public class BookEventMessage extends EventMessage {
    public final ActorRef origin;

    public BookEventMessage(Event event, ActorRef origin) {
        super(event);
        this.origin = origin;
    }
}
