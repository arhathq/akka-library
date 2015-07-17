package library.core.eaa;

/**
 * @author Alexander Kuleshov
 */
public interface Action<A extends Activity, E extends Event> {

    A perform(E event);

}
