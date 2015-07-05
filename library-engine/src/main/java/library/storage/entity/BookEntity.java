package library.storage.entity;

import library.domain.Author;
import library.domain.Book;
import library.core.data.Versioned;
import library.domain.Publisher;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Alexander Kuleshov
 */
@Entity
@Table(name = "Book")
public class BookEntity implements Book, Versioned {

    @Id
    @GeneratedValue
    private Long id;
    private String title;

    @ManyToMany(targetEntity = AuthorEntity.class, fetch = FetchType.EAGER)
    @JoinTable(name = "Author_Book", joinColumns = @JoinColumn(name="book_id"), inverseJoinColumns=@JoinColumn(name="author_id"))
    private Set<Author> authors = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = PublisherEntity.class)
    private Publisher publisher;

    @Version
    private int version;

    public BookEntity() {
    }

    public BookEntity(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.authors.addAll(book.getAuthors());
        if (book.getPublisher() != null) {
            this.publisher = new PublisherEntity(book.getPublisher());
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    @Override
    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
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
        if (!(o instanceof BookEntity)) return false;
        BookEntity that = (BookEntity) o;
        return Objects.equals(version, that.version) &&
                Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(authors, that.authors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, authors, version);
    }
}