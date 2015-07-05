package library.storage.dao;

import library.storage.entity.PublisherEntity;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * @author Alexander Kuleshov
 */
public interface PublisherDao extends Repository<PublisherEntity, Long> {

    List<PublisherEntity> findAll();

    List<PublisherEntity> findAll(List<Long> ids);

    PublisherEntity findOne(Long id);

    PublisherEntity save(PublisherEntity publisher);

    void delete(PublisherEntity publisher);

}
