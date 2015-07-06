package library.storage;

import library.engine.LibraryException;

/**
 * @author Alexander Kuleshov
 */
public class StorageException extends LibraryException {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
