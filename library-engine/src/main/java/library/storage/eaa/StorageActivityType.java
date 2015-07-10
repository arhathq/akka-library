package library.storage.eaa;

import library.core.eaa.ActivityType;

/**
 * @author Alexander Kuleshov
 */
public enum StorageActivityType implements ActivityType {

    BOOKS_RETURNED,
    BOOK_RETURNED,
    BOOK_SAVED,
    AUTHORS_RETURNED,
    AUTHOR_RETURNED,
    AUTHOR_SAVED,
    PUBLISHERS_RETURNED,
    PUBLISHER_RETURNED,
    PUBLISHER_SAVED

}