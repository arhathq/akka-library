package library.book.dao;

import library.domain.Book;
import library.core.data.Persistent;
import library.core.data.Versioned;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Alexander Kuleshov
 */
@Entity
@Table(name = "Book")
public class BookEntity implements Book, Persistent<Long>, Versioned {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String author;

    @Version
    private int version;

    public BookEntity() {
    }

    public BookEntity(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return null == id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
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
                Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, version);
    }
}
