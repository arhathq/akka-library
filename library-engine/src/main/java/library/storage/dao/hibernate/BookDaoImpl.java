package library.storage.dao.hibernate;

import library.domain.Book;
import library.storage.dao.BookDao;
import library.storage.entity.BookEntity;
import org.springframework.stereotype.Repository;
import library.domain.BookSearchRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author Alexander Kuleshov
 */
@Repository
public class BookDaoImpl implements BookDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<BookEntity> findAll() {
        return em.createQuery("Select b from BookEntity b", BookEntity.class).getResultList();
    }

    @Override
    public List<BookEntity> findAll(List<Long> ids) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BookEntity> criteriaQuery = cb.createQuery(BookEntity.class);
        Root<BookEntity> from = criteriaQuery.from(BookEntity.class);
        criteriaQuery.select(from);
        criteriaQuery.where(from.in(ids));

        return em.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<BookEntity> findAll(BookSearchRequest request) {
        return em.createQuery("Select b from BookEntity b", BookEntity.class).getResultList();
    }

    @Override
    public BookEntity findOne(Long id) {
        return em.find(BookEntity.class, id);
    }

    @Override
    public BookEntity save(BookEntity book) {
        return em.merge(book);
    }

    @Override
    public BookEntity toEntity(Book book) {
        if (book.getId() == null) {
            return new BookEntity(book);
        }

        BookEntity entity = findOne(book.getId());
        if (entity == null) {
            return new BookEntity(book);

        }

        return entity.from(book);
    }

    @Override
    public void delete(BookEntity detached) {
        BookEntity persisted = em.merge(detached);
        em.remove(persisted);
    }

    @Override
    public void deleteAll() {
        em.createNativeQuery("Delete from Author_Book").executeUpdate();
        em.createNativeQuery("Delete from Book").executeUpdate();
    }
}
