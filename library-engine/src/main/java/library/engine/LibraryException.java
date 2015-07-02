package library.engine;

import library.core.ApplicationException;

/**
 * @author Alexander Kuleshov
 */
public class LibraryException extends ApplicationException {
    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
