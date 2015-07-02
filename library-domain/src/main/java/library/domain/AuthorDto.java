package library.domain;

/**
 * @author Alexander Kuleshov
 */
public class AuthorDto implements Author {

    private Long id;
    private String firstName;
    private String lastName;

    public AuthorDto() {
    }

    public AuthorDto(Author author) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}
