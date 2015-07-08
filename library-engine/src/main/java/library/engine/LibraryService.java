package library.engine;

import library.domain.Author;
import library.domain.Book;
import library.domain.BookSearchRequest;
import library.domain.Publisher;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Alexander Kuleshov
 */
public interface LibraryService {

    Future<List<Book>> getBooks(BookSearchRequest request) throws LibraryException;

    Future<Long> saveBook(Book book) throws LibraryException;

    Future<List<Author>> getAuthors() throws LibraryException;

    Future<List<Publisher>> getPublishers() throws LibraryException;

}
