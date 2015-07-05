package library.domain;

import java.io.Serializable;

/**
 * @author Alexander Kuleshov
 */
public interface Publisher extends Serializable {
    Long getId();

    String getName();
}
