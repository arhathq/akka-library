package library.core.eaa;

/**
 * @author Alexander Kuleshov
 */
public interface Action {

    Activity perform(Event bookEvent);

}
