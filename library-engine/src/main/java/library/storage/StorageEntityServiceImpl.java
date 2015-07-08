package library.storage;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import library.domain.Author;
import library.domain.Book;
import library.domain.BookSearchRequest;
import library.domain.Publisher;
import library.domain.dto.AuthorDto;
import library.domain.dto.BookDto;
import library.domain.dto.PublisherDto;
import library.storage.dao.AuthorDao;
import library.storage.dao.BookDao;
import library.storage.dao.PublisherDao;
import library.storage.entity.AuthorEntity;
import library.storage.entity.BookEntity;
import library.storage.entity.PublisherEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alexander Kuleshov
 */
@Service
public class StorageEntityServiceImpl implements StorageEntityService {

    @Autowired
    private BookDao bookDao;

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private PublisherDao publisherDao;

    @Override
    public List<Book> getBooks(BookSearchRequest bookRequest) throws StorageException {
        List<BookEntity> bookEntities = bookDao.findAll(bookRequest);

        List<Book> books = Lists.transform(bookEntities, new Function<BookEntity, Book>() {
            @Override
            public Book apply(BookEntity bookEntity) {
                return new BookDto(bookEntity);
            }
        });
        return books;
    }

    @Override
    @Transactional(readOnly = false)
    public Book saveBook(Book book) throws StorageException {
        BookEntity persistedBook = bookDao.toEntity(book);
        return bookDao.save(persistedBook);
    }

    @Override
    public List<Author> getAuthors() throws StorageException {
        List<AuthorEntity> authorEntities = authorDao.findAll();

        return Lists.transform(authorEntities, new Function<AuthorEntity, Author>() {
            @Override
            public Author apply(AuthorEntity authorEntity) {
                return new AuthorDto(authorEntity);
            }
        });
    }

    @Override
    public List<Publisher> getPublishers() throws StorageException {
        List<PublisherEntity> publisherEntities = publisherDao.findAll();

        return Lists.transform(publisherEntities, new Function<PublisherEntity, Publisher>() {
            @Override
            public Publisher apply(PublisherEntity publisherEntity) {
                return new PublisherDto(publisherEntity);
            }
        });
    }
}