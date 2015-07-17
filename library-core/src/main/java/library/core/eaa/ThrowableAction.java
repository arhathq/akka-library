package library.core.eaa;

/**
 * @author Alexander Kuleshov
 */
public abstract class ThrowableAction<E extends Event> implements Action<Activity, E> {
    @Override
    public Activity perform(E event) {
        try {
            return doPerform(event);
        } catch (Throwable t) {
            return Activities.createErrorActivity(t);
        }
    }

    public abstract Activity doPerform(final E event) throws Throwable;
}
