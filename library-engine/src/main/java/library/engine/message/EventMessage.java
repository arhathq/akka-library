package library.engine.message;

import library.core.eaa.Event;

/**
 * @author Alexander Kuleshov
 */
public class EventMessage {
    public final Event event;

    public EventMessage(Event event) {
        this.event = event;
    }
}
