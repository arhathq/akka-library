package library.domain;

/**
 * @author Alexander Kuleshov
 */
public final class AuthorDto implements Author {

    private final Long id;
    private final String firstName;
    private final String lastName;

    public AuthorDto(Author author) {
        this.id = author.getId();
        this.firstName = author.getFirstName();
        this.lastName = author.getLastName();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

}
