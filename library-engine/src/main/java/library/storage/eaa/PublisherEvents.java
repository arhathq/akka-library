package library.storage.eaa;

import library.core.eaa.Event;
import library.core.eaa.EventType;
import library.domain.Publisher;

/**
 * @author Alexander Kuleshov
 */
public class PublisherEvents {
    public static GetPublishers createGetPublishersEvent() {
        return new GetPublishers();
    }

    public static GetPublisher createGetPublisherEvent(Long id) {
        return new GetPublisher(id);
    }

    public static SavePublisher createSavePublisherEvent(Publisher publisher) {
        return new SavePublisher(publisher);
    }

    public static class GetPublishers implements Event {
        @Override
        public EventType getEventType() {
            return StorageEventType.GET_PUBLISHERS;
        }
    }

    public static class GetPublisher implements Event {
        public final Long id;

        public GetPublisher(Long id) {
            this.id = id;
        }

        @Override
        public EventType getEventType() {
            return StorageEventType.GET_PUBLISHER;
        }
    }

    public static class SavePublisher implements Event {
        public final Publisher publisher;

        public SavePublisher(Publisher publisher) {
            this.publisher = publisher;
        }

        @Override
        public EventType getEventType() {
            return StorageEventType.SAVE_PUBLISHER;
        }
    }

}
