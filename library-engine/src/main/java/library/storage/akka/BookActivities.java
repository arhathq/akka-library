package library.storage.akka;

import library.domain.Book;
import library.core.eaa.Activity;
import library.storage.BookActivityType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Kuleshov
 */
public class BookActivities {

    public static GetBooksActivity createGetBooksActivity(List<Book> books) {
        return new GetBooksActivity(books);
    }

    public static SaveBookActivity createSaveBookActivity(Book book) {
        return new SaveBookActivity(book);
    }

    private static class GetBooksActivity implements Activity {
        private final List<Book> books;

        public GetBooksActivity(List<Book> books) {
            this.books = new ArrayList<>(books);
        }

        @Override
        public BookActivityType getActivityType() {
            return BookActivityType.BOOKS_RETURNED;
        }

        @Override
        public Object getData() {
            return books;
        }
    }

    private static class SaveBookActivity implements Activity {
        private final Book book;

        public SaveBookActivity(Book book) {
            this.book = book;
        }

        @Override
        public BookActivityType getActivityType() {
            return BookActivityType.BOOK_SAVED;
        }

        @Override
        public Object getData() {
            return book;
        }
    }
}
