package library.storage.dao;

import library.storage.entity.AuthorEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
public class AuthorDaoImpl implements AuthorDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<AuthorEntity> findAll() {
        return em.createQuery("Select a from AuthorEntity a", AuthorEntity.class).getResultList();
    }

    @Override
    public List<AuthorEntity> findAll(List<Long> ids) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AuthorEntity> criteriaQuery = cb.createQuery(AuthorEntity.class);
        Root<AuthorEntity> from = criteriaQuery.from(AuthorEntity.class);
        criteriaQuery.select(from);
        criteriaQuery.where(from.in(ids));

        return em.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public AuthorEntity findOne(Long id) {
        return em.find(AuthorEntity.class, id);
    }

    @Override
    @Transactional(readOnly = false)
    public AuthorEntity save(AuthorEntity entity) {
        return em.merge(entity);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(AuthorEntity detached) {
        AuthorEntity persisted = em.merge(detached);
        em.remove(persisted);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAll() {
        em.createQuery("Delete from AuthorEntity").executeUpdate();
    }
}