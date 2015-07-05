package library.domain;

import java.util.Collection;

/**
 * @author Alexander Kuleshov
 */
public class BookDto implements Book {

    private Long id;
    private String title;
    private Collection<Author> authors;
    private Publisher publisher;

    public BookDto() {
    }

    public BookDto(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.authors = book.getAuthors();
        this.publisher = book.getPublisher();
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
    public Collection<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<Author> author) {
        this.authors = author;
    }

    @Override
    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}