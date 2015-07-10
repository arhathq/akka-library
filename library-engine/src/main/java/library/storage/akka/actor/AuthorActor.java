package library.storage.akka.actor;

import library.core.eaa.AbstractAction;
import library.core.eaa.Activities;
import library.core.eaa.Activity;
import library.core.eaa.Event;
import library.domain.Author;
import library.storage.StorageException;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.akka.message.StorageEventMessage;
import library.storage.eaa.AuthorActivities;
import library.storage.eaa.AuthorEvents;
import library.storage.eaa.StorageEventType;

import java.util.List;

/**
 * @author Alexander Kuleshov
 */
public class AuthorActor extends StorageActor {

    @Override
    protected StorageActivityMessage performActivityByEventDecision(StorageEventMessage eventMessage) {
        Event event = eventMessage.event;

        Activity activity;
        if (StorageEventType.GET_AUTHORS == event.getEventType()) {
            activity = new GetAuthorsAction().perform(event);
        } else if (StorageEventType.GET_AUTHOR == event.getEventType()) {
            activity = new GetAuthorAction().perform(event);
        } else if (StorageEventType.SAVE_AUTHOR == event.getEventType()) {
            activity = new SaveAuthorAction().perform(event);
        } else {
            activity = Activities.createErrorActivity(new StorageException("Unsupported event: " + event.getEventType()));
        }
        return new StorageActivityMessage(activity);
    }

    private class GetAuthorsAction extends AbstractAction {
        @Override
        public Activity doPerform(Event event) throws Throwable {
            List<Author> authors = entityService.getAuthors();
            return AuthorActivities.createGetAuthorsActivity(authors);
        }
    }

    private class GetAuthorAction extends AbstractAction {
        @Override
        public Activity doPerform(Event event) throws Throwable {
            Author author = entityService.getAuthor(((AuthorEvents.GetAuthor) event).id);
            return AuthorActivities.createGetAuthorActivity(author);
        }
    }

    private class SaveAuthorAction extends AbstractAction {
        @Override
        public Activity doPerform(Event event) throws Throwable {
            Author author = entityService.saveAuthor(((AuthorEvents.SaveAuthor) event).author);
            return AuthorActivities.createSaveAuthorActivity(author);
        }
    }
}