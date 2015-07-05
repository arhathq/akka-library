package library.storage.dao;

import library.storage.entity.AuthorEntity;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * @author Alexander Kuleshov
 */
public interface AuthorDao extends Repository<AuthorEntity, Long> {

    List<AuthorEntity> findAll();

    List<AuthorEntity> findAll(List<Long> ids);

    AuthorEntity findOne(Long id);

    AuthorEntity save(AuthorEntity author);

    void delete(AuthorEntity author);

}
