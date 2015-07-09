package library.core.eaa;

/**
 * @author Alexander Kuleshov
 */
public abstract class AbstractThrowableActivity implements ThrowableActivity {

    private final Throwable throwable;

    public AbstractThrowableActivity(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }
}
