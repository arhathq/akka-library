package library.storage.eaa;

import library.core.eaa.Event;
import library.core.eaa.EventType;
import library.domain.Author;

/**
 * @author Alexander Kuleshov
 */
public class AuthorEvents {
    public static GetAuthors createGetAuthorsEvent() {
        return new GetAuthors();
    }

    public static GetAuthor createGetAuthorEvent(Long id) {
        return new GetAuthor(id);
    }

    public static SaveAuthor createSaveAuthorEvent(Author author) {
        return new SaveAuthor(author);
    }

    public static class GetAuthors implements Event {
        @Override
        public EventType getEventType() {
            return StorageEventType.GET_AUTHORS;
        }
    }

    public static class GetAuthor implements Event {
        public final Long id;

        public GetAuthor(Long id) {
            this.id = id;
        }

        @Override
        public EventType getEventType() {
            return StorageEventType.GET_AUTHOR;
        }
    }

    public static class SaveAuthor implements Event {
        public final Author author;

        public SaveAuthor(Author author) {
            this.author = author;
        }

        @Override
        public EventType getEventType() {
            return StorageEventType.SAVE_AUTHOR;
        }
    }
}
