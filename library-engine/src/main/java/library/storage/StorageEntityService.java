package library.storage;

import library.domain.Author;
import library.domain.Book;
import library.domain.BookSearchRequest;
import library.domain.Publisher;

import java.util.List;

/**
 * @author Alexander Kuleshov
 */
public interface StorageEntityService {

    List<Book> getBooks(BookSearchRequest bookRequest) throws StorageException;

    Book getBook(Long id) throws StorageException;

    Book saveBook(Book book) throws StorageException;

    List<Author> getAuthors() throws StorageException;

    Author getAuthor(Long id) throws StorageException;

    Author saveAuthor(Author author) throws StorageException;

    List<Publisher> getPublishers() throws StorageException;

    Publisher getPublisher(Long id) throws StorageException;

    Publisher savePublisher(Publisher publisher) throws StorageException;

}