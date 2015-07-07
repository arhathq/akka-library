package library.domain.dto;

import library.domain.Publisher;

/**
 * @author Alexander Kuleshov
 */
public final class PublisherDto implements Publisher {
    private final Long id;
    private final String name;

    public PublisherDto(Publisher publisher) {
        this.id = publisher.getId();
        this.name = publisher.getName();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
