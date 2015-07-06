package library.engine.message;

import library.core.eaa.Activity;

/**
 * @author Alexander Kuleshov
 */
public class ActivityMessage {
    public final Activity activity;

    public ActivityMessage(Activity activity) {
        this.activity = activity;
    }
}
