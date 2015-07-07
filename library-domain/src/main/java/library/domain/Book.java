package library.domain;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Alexander Kuleshov
 */
public interface Book extends Serializable {

    Long getId();

    String getTitle();

    Collection<? extends Author> getAuthors();

    Publisher getPublisher();

}