package library.engine;

import library.domain.Author;
import library.domain.Book;
import library.domain.BookSearchRequest;
import library.domain.Publisher;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

/**
 * @author Alexander Kuleshov
 */
public interface LibraryService {

    ListenableFuture<List<Book>> getBooks(BookSearchRequest request) throws LibraryException;

    ListenableFuture<Book> getBook(Long id) throws LibraryException;

    ListenableFuture<Long> saveBook(Book book) throws LibraryException;

    ListenableFuture<List<Author>> getAuthors() throws LibraryException;

    ListenableFuture<Author> getAuthor(Long id) throws LibraryException;

    ListenableFuture<Long> saveAuthor(Author author) throws LibraryException;

    ListenableFuture<List<Publisher>> getPublishers() throws LibraryException;

    ListenableFuture<Publisher> getPublisher(Long id) throws LibraryException;

    ListenableFuture<Long> savePublisher(Publisher publisher) throws LibraryException;

}
