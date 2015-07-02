package library.storage.akka.message;

import library.core.eaa.Activity;

/**
 * @author Alexander Kuleshov
 */
public class BookActivityMessage {
    public final Activity activity;

    public BookActivityMessage(final Activity activity) {
        this.activity = activity;
    }
}