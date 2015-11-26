package library.storage.akka.actor;

import library.core.eaa.ThrowableAction;
import library.core.eaa.Activities;
import library.core.eaa.Activity;
import library.core.eaa.Event;
import library.domain.Author;
import library.storage.StorageException;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.akka.message.StorageEventMessage;
import library.storage.eaa.AuthorActivities;
import library.storage.eaa.StorageEventType;

import java.util.List;

import static library.storage.eaa.AuthorEvents.*;

/**
 * @author Alexander Kuleshov
 */
public class AuthorActor extends StorageActor {

    @Override
    protected StorageActivityMessage performActivityByEventDecision(StorageEventMessage eventMessage) {
        Event event = eventMessage.event;

        Activity activity;
        if (StorageEventType.GET_AUTHORS == event.getEventType()) {
            activity = new GetAuthorsAction().perform((GetAuthors) event);
        } else if (StorageEventType.GET_AUTHOR == event.getEventType()) {
            activity = new GetAuthorAction().perform((GetAuthor) event);
        } else if (StorageEventType.SAVE_AUTHOR == event.getEventType()) {
            activity = new SaveAuthorAction().perform((SaveAuthor) event);
        } else {
            activity = Activities.createErrorActivity(new StorageException("Unsupported event: " + event.getEventType()));
        }
        return createActivityMessage(eventMessage.id, activity, eventMessage.origin);
    }

    private class GetAuthorsAction extends ThrowableAction<GetAuthors> {
        @Override
        public Activity doPerform(GetAuthors event) throws Throwable {
            List<Author> authors = entityService.getAuthors();
            return AuthorActivities.createGetAuthorsActivity(authors);
        }
    }

    private class GetAuthorAction extends ThrowableAction<GetAuthor> {
        @Override
        public Activity doPerform(GetAuthor event) throws Throwable {
            Author author = entityService.getAuthor(event.id);
            return AuthorActivities.createGetAuthorActivity(author);
        }
    }

    private class SaveAuthorAction extends ThrowableAction<SaveAuthor> {
        @Override
        public Activity doPerform(SaveAuthor event) throws Throwable {
            Author author = entityService.saveAuthor(event.author);
            return AuthorActivities.createSaveAuthorActivity(author);
        }
    }
}