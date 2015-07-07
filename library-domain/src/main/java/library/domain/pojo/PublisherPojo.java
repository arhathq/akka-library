package library.domain.pojo;

import library.domain.Publisher;

/**
 * @author Alexander Kuleshov
 */
public class PublisherPojo implements Publisher {

    private Long id;
    private String name;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
