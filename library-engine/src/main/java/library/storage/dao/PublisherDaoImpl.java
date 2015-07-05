package library.storage.dao;

import library.storage.entity.PublisherEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Alexander Kuleshov
 */
@Repository
public class PublisherDaoImpl implements PublisherDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<PublisherEntity> findAll() {
        return em.createQuery("Select a from PublisherEntity a", PublisherEntity.class).getResultList();
    }

    @Override
    public List<PublisherEntity> findAll(List<Long> ids) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PublisherEntity findOne(Long id) {
        return em.find(PublisherEntity.class, id);
    }

    @Override
    @Transactional(readOnly = false)
    public PublisherEntity save(PublisherEntity entity) {
        return em.merge(entity);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(PublisherEntity publisher) {
        PublisherEntity persisted = em.merge(publisher);
        em.remove(persisted);
    }
}
