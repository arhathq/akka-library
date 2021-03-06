package library.storage.dao;

import library.domain.Book;
import library.storage.entity.BookEntity;
import org.springframework.data.repository.Repository;
import library.domain.BookSearchRequest;

import java.util.List;

/**
 * @author Alexander Kuleshov
 */
public interface BookDao extends Repository<BookEntity, Long> {

    List<BookEntity> findAll();

    List<BookEntity> findAll(List<Long> ids);

    List<BookEntity> findAll(BookSearchRequest request);

    BookEntity findOne(Long id);

    BookEntity save(BookEntity book);

    BookEntity toEntity(Book book);

    void delete(BookEntity book);

    void deleteAll();
}