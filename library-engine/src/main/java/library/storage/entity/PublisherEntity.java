package library.storage.entity;

import library.domain.Publisher;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        return result;
    }
}
