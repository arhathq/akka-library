package library.storage.entity;

import library.domain.Publisher;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author Alexander Kuleshov
 */
@Entity
@Table(name = "Publisher")
public class PublisherEntity implements Publisher {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    public PublisherEntity() {
    }

    public PublisherEntity(Publisher publisher) {
        this.id = publisher.getId();
        this.name = publisher.getName();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublisherEntity)) return false;
        PublisherEntity that = (PublisherEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
