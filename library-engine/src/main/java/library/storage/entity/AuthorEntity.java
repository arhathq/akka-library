package library.storage.entity;

import library.domain.Author;
import library.storage.dao.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author Alexander Kuleshov
 */
@Entity
@Table(name = "Author")
public class AuthorEntity extends AbstractEntity implements Author {

    private String firstName;
    private String lastName;

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthorEntity)) return false;
        AuthorEntity that = (AuthorEntity) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), firstName, lastName);
    }
}
