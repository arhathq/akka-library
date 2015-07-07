package library.domain.dto;

import library.domain.Author;
import library.domain.Book;
import library.domain.Publisher;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Alexander Kuleshov
 */
public final class BookDto implements Book {

    private final Long id;
    private final String title;
    private final Collection<Author> authors = new HashSet<>();
    private final Publisher publisher;

    public BookDto(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.authors.addAll(book.getAuthors());
        this.publisher = book.getPublisher();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Collection<Author> getAuthors() {
        return authors;
    }

    @Override
    public Publisher getPublisher() {
        return publisher;
    }
}