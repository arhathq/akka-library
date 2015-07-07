package library.core.eaa;

/**
 * @author Alexander Kuleshov
 */
public abstract class AbstractActivity implements Activity {
    private Throwable throwable;

    public AbstractActivity() {
    }

    public AbstractActivity(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }
}
