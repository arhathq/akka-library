package library.domain.pojo;

import library.domain.Author;

/**
 * @author Alexander Kuleshov
 */
public class AuthorPojo implements Author {

    private Long id;
    private String firstName;
    private String lastName;

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
