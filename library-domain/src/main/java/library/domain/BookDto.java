package library.domain;

import java.util.Collection;

/**
 * @author Alexander Kuleshov
 */
public class BookDto implements Book {

    private Long id;
    private String title;
    private Collection<Author> author;

    public BookDto() {
    }

    public BookDto(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthors();
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
        return author;
    }

    public void setAuthor(Collection<Author> author) {
        this.author = author;
    }
}