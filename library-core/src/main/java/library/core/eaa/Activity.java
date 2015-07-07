package library.core.eaa;

/**
 * @author Alexander Kuleshov
 */
public interface Activity {

//    String getActivityId();

//    String getActivityClass();

    ActivityType getActivityType();

//    String getEventId();

    Object getPayload();

    Throwable getThrowable();

}
