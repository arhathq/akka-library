package library.storage.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import library.domain.BookSearchRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
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
        ArrayList<BookEntity> books = new ArrayList<>();

        return em.createQuery("Select b from BookEntity b", BookEntity.class).getResultList();
    }

    @Override
    public BookEntity findOne(Long id) {
        return em.find(BookEntity.class, id);
    }

    @Transactional(readOnly = false)
    @Override
    public BookEntity save(BookEntity book) {
        if (book.isNew()) {
            em.persist(book);
            return book;
        }
        return em.merge(book);
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteAll() {
        em.createQuery("Delete from BookEntity").executeUpdate();
    }
}
