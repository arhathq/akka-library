package library.domain;

import java.io.Serializable;

/**
 * @author Alexander Kuleshov
 */
public interface Author extends Serializable {

    Long getId();

    String getFirstName();

    String getLastName();

}
