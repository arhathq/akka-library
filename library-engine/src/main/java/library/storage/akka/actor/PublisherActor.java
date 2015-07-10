package library.storage.akka.actor;

import library.core.eaa.AbstractAction;
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

/**
 * @author Alexander Kuleshov
 */
public class PublisherActor extends StorageActor {

    @Override
    protected StorageActivityMessage performActivityByEventDecision(StorageEventMessage eventMessage) {
        Event event = eventMessage.event;

        Activity activity;
        if (StorageEventType.GET_PUBLISHERS == event.getEventType()) {
            activity = new GetPublishersAction().perform(event);
        } else if (StorageEventType.GET_PUBLISHER == event.getEventType()) {
            activity = new GetPublisherAction().perform(event);
        } else if (StorageEventType.SAVE_PUBLISHER == event.getEventType()) {
            activity = new SavePublisherAction().perform(event);
        } else {
            activity = Activities.createErrorActivity(new StorageException("Unsupported event: " + event.getEventType()));
        }
        return new StorageActivityMessage(activity);
    }

    private class GetPublishersAction extends AbstractAction {
        @Override
        public Activity doPerform(Event event) throws Throwable {
            List<Publisher> publishers = entityService.getPublishers();
            return PublisherActivities.createGetPublishersActivity(publishers);
        }
    }

    private class GetPublisherAction extends AbstractAction {
        @Override
        public Activity doPerform(Event event) throws Throwable {
            Publisher publisher = entityService.getPublisher(((PublisherEvents.GetPublisher) event).id);
            return PublisherActivities.createGetPublisherActivity(publisher);
        }
    }

    private class SavePublisherAction extends AbstractAction {
        @Override
        public Activity doPerform(Event event) throws Throwable {
            Publisher publisher = entityService.savePublisher(((PublisherEvents.SavePublisher) event).publisher);
            return PublisherActivities.createSavePublisherActivity(publisher);
        }
    }
}
