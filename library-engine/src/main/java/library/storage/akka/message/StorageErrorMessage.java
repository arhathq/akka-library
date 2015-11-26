package library.storage.akka.message;

import akka.actor.ActorRef;
import library.core.eaa.ThrowableActivity;

/**
 * @author Alexander Kuleshov
 */
public class StorageErrorMessage extends StorageActivityMessage {
    public final ActorRef destination;

    public StorageErrorMessage(long id, ThrowableActivity activity, ActorRef destination) {
        super(id, activity);
        this.destination = destination;
    }

    public ThrowableActivity getThrowableActivity() {
        return (ThrowableActivity) super.activity;
    }
}
