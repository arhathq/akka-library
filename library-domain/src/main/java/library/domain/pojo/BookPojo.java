package library.domain.pojo;

import library.domain.Author;
import library.domain.Book;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Alexander Kuleshov
 */
public class BookPojo implements Book {
    private Long id;
    private String title;
    private Collection<AuthorPojo> authors = new HashSet<>();
    private PublisherPojo publisher;

    public BookPojo() {
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
    public Collection<? extends Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<AuthorPojo> authors) {
        this.authors = authors;
    }

    @Override
    public PublisherPojo getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherPojo publisher) {
        this.publisher = publisher;
    }
}