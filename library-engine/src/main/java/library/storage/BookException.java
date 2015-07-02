package library.storage;

import library.engine.LibraryException;

/**
 * @author Alexander Kuleshov
 */
public class BookException extends LibraryException {
    public BookException(String message) {
        super(message);
    }

    public BookException(String message, Throwable cause) {
        super(message, cause);
    }
}
