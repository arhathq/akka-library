package library.storage.akka.message;

import library.core.eaa.Activity;
import library.engine.message.ActivityMessage;

/**
 * @author Alexander Kuleshov
 */
public class StorageActivityMessage extends ActivityMessage {
    public final long id;

    public StorageActivityMessage(long id, final Activity activity) {
        super(activity);
        this.id = id;
    }
}