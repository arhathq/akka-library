package library.storage.entity;

import library.domain.Author;
import library.domain.Book;
import library.core.data.Versioned;
import library.storage.dao.AbstractEntity;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * @author Alexander Kuleshov
 */
@Entity
@Table(name = "Book")
public class BookEntity extends AbstractEntity implements Book, Versioned {

    private String title;

    private Set<Author> author;

    @Version
    private int version;

    public BookEntity() {
    }

    public BookEntity(Book book) {
        this.setId(book.getId());
        this.title = book.getTitle();
        this.author.addAll(book.getAuthors());
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    @OneToMany
    public Set<Author> getAuthors() {
        return author;
    }

    public void setAuthor(Set<Author> author) {
        this.author = author;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookEntity that = (BookEntity) o;
        return Objects.equals(version, that.version) &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(title, that.title) &&
                Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), title, author, version);
    }
}