package library.storage.eaa;

import library.core.eaa.Activity;
import library.domain.Publisher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Kuleshov
 */
public class PublisherActivities {
    public static Activity createGetPublishersActivity(List<Publisher> publishers) {
        return new GetPublishersActivity(publishers);
    }

    public static Activity createGetPublisherActivity(Publisher publisher) {
        return new GetPublisherActivity(publisher);
    }

    public static Activity createSavePublisherActivity(Publisher publisher) {
        return new SavePublisherActivity(publisher);
    }

    public static class GetPublishersActivity implements Activity {
        private final List<Publisher> publishers;

        public GetPublishersActivity(List<Publisher> publishers) {
            this.publishers = new ArrayList<>(publishers);
        }

        @Override
        public StorageActivityType getActivityType() {
            return StorageActivityType.PUBLISHERS_RETURNED;
        }

        @Override
        public Object getPayload() {
            return publishers;
        }
    }

    public static class GetPublisherActivity implements Activity {
        private final Publisher publisher;

        public GetPublisherActivity(Publisher publisher) {
            this.publisher = publisher;
        }

        @Override
        public StorageActivityType getActivityType() {
            return StorageActivityType.PUBLISHER_RETURNED;
        }

        @Override
        public Object getPayload() {
            return publisher;
        }
    }

    public static class SavePublisherActivity implements Activity {
        private final Publisher publisher;

        public SavePublisherActivity(Publisher publisher) {
            this.publisher = publisher;
        }

        @Override
        public StorageActivityType getActivityType() {
            return StorageActivityType.PUBLISHER_SAVED;
        }

        @Override
        public Object getPayload() {
            return publisher;
        }
    }

}