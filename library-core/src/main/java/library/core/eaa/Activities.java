package library.core.eaa;

/**
 * @author Alexander Kuleshov
 */
public class Activities {

    private Activities() {}

    public static AckActivity createAckActivity() {
        return new AckActivity();
    }

    public static <A extends ErrorActivity> ErrorActivity createErrorActivity(Throwable t) {
        return new ErrorActivity(t);
    }

    public static class AckActivity implements Activity {

        @Override
        public ActivityType getActivityType() {
            return SystemActivityType.ACK;
        }

        @Override
        @SuppressWarnings("unchecked")
        public String getPayload() {
            return "ack";
        }
    }

    public static class ErrorActivity extends AbstractThrowableActivity {
        public ErrorActivity(Throwable t) {
            super(t);
        }

        @Override
        public ActivityType getActivityType() {
            return SystemActivityType.ERROR;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object getPayload() {
            return null;
        }
    }
}