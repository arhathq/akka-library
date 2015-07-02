package library.engine;

import library.domain.Book;
import library.domain.BookSearchRequest;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Alexander Kuleshov
 */
public interface LibraryService {

    Future<List<Book>> getBooks(BookSearchRequest request) throws LibraryException;

    Future<Long> saveBook(Book book) throws LibraryException;
}
