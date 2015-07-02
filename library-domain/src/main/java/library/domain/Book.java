package library.domain;

import java.io.Serializable;

/**
 * @author Alexander Kuleshov
 */
public interface Book extends Serializable {

    Long getId();

    String getTitle();

    String getAuthor();
}
