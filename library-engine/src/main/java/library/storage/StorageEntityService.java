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

    Book saveBook(Book book) throws StorageException;

    List<Author> getAuthors() throws StorageException;

    List<Publisher> getPublishers() throws StorageException;

}