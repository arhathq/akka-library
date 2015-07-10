package library.storage.eaa;

import library.domain.Book;
import library.domain.BookSearchRequest;
import library.core.eaa.Event;
import library.core.eaa.EventType;

/**
 * @author Alexander Kuleshov
 */
public class BookEvents {

    public static GetBooks createGetBooksEvent(final BookSearchRequest request) {
        return new GetBooks(request);
    }

    public static GetBook createGetBookEvent(Long id) {
        return new GetBook(id);
    }

    public static SaveBook createSaveBookEvent(final Book book) {
        return new SaveBook(book);
    }

    public static class GetBooks implements Event {
        public final BookSearchRequest searchRequest;

        public GetBooks(BookSearchRequest searchRequest) {
            this.searchRequest = searchRequest;
        }

        @Override
        public EventType getEventType() {
            return StorageEventType.GET_BOOKS;
        }
    }

    public static class SaveBook implements Event {
        public final Book book;

        public SaveBook(Book book) {
            this.book = book;
        }

        @Override
        public EventType getEventType() {
            return StorageEventType.SAVE_BOOK;
        }
    }

    public static class GetBook implements Event {
        public final Long bookId;

        public GetBook(Long bookId) {
            this.bookId = bookId;
        }

        @Override
        public EventType getEventType() {
            return StorageEventType.GET_BOOK;
        }
    }
}
