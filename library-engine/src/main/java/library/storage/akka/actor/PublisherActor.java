package library.storage.akka.actor;

import library.core.eaa.ThrowableAction;
import library.core.eaa.Activities;
import library.core.eaa.Activity;
import library.core.eaa.Event;
import library.domain.Publisher;
import library.storage.StorageException;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.akka.message.StorageEventMessage;
import library.storage.eaa.PublisherActivities;
import library.storage.eaa.PublisherEvents;
import library.storage.eaa.StorageEventType;

import java.util.List;

import static library.storage.eaa.PublisherEvents.*;

/**
 * @author Alexander Kuleshov
 */
public class PublisherActor extends StorageActor {

    @Override
    protected StorageActivityMessage performActivityByEventDecision(StorageEventMessage eventMessage) {
        Event event = eventMessage.event;

        Activity activity;
        if (StorageEventType.GET_PUBLISHERS == event.getEventType()) {
            activity = new GetPublishersAction().perform((GetPublishers) event);
        } else if (StorageEventType.GET_PUBLISHER == event.getEventType()) {
            activity = new GetPublisherAction().perform((GetPublisher) event);
        } else if (StorageEventType.SAVE_PUBLISHER == event.getEventType()) {
            activity = new SavePublisherAction().perform((SavePublisher) event);
        } else {
            activity = Activities.createErrorActivity(new StorageException("Unsupported event: " + event.getEventType()));
        }
        return createActivityMessage(activity, eventMessage.origin);
    }

    private class GetPublishersAction extends ThrowableAction<GetPublishers> {
        @Override
        public Activity doPerform(GetPublishers event) throws Throwable {
            List<Publisher> publishers = entityService.getPublishers();
            return PublisherActivities.createGetPublishersActivity(publishers);
        }
    }

    private class GetPublisherAction extends ThrowableAction<GetPublisher> {
        @Override
        public Activity doPerform(GetPublisher event) throws Throwable {
            Publisher publisher = entityService.getPublisher(event.id);
            return PublisherActivities.createGetPublisherActivity(publisher);
        }
    }

    private class SavePublisherAction extends ThrowableAction<SavePublisher> {
        @Override
        public Activity doPerform(SavePublisher event) throws Throwable {
            Publisher publisher = entityService.savePublisher(event.publisher);
            return PublisherActivities.createSavePublisherActivity(publisher);
        }
    }
}
