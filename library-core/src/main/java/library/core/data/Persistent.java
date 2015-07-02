package library.core.data;

import java.io.Serializable;

/**
 * @author Alexander Kuleshov
 */
public interface Persistent<V> extends Serializable {
    V getId();

    boolean isNew();
}
