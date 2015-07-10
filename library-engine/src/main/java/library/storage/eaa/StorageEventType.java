package library.storage.eaa;

import library.core.eaa.EventType;

/**
 * @author Alexander Kuleshov
 */
public enum StorageEventType implements EventType {

    GET_BOOKS,
    GET_AUTHORS,
    GET_PUBLISHERS,
    GET_BOOK,
    GET_AUTHOR,
    GET_PUBLISHER,
    SAVE_BOOK,
    SAVE_AUTHOR,
    SAVE_PUBLISHER

}