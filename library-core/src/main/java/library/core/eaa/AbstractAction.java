package library.core.eaa;

/**
 * @author Alexander Kuleshov
 */
public abstract class AbstractAction implements Action {
    @Override
    public Activity perform(Event event) {
        try {
            return doPerform(event);
        } catch (Throwable t) {
            return Activities.createErrorActivity(t);
        }
    }

    public abstract Activity doPerform(final Event event) throws Throwable;
}
