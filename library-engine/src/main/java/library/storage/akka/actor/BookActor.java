package library.storage.akka.actor;

import library.core.eaa.*;
import library.domain.Book;
import library.domain.dto.BookDto;
import library.storage.StorageException;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.akka.message.StorageEventMessage;
import library.storage.eaa.BookActivities;
import library.storage.eaa.BookEvents;
import library.storage.eaa.StorageEventType;

import java.util.List;

/**
 * @author Alexander Kuleshov
 */
public class BookActor extends StorageActor {

    @Override
    protected StorageActivityMessage performActivityByEventDecision(StorageEventMessage eventMessage) {
        Event event = eventMessage.event;

        Activity activity;
        if (StorageEventType.GET_BOOKS == event.getEventType()) {
            activity = new GetBooksAction().perform(event);
        } else if (StorageEventType.SAVE_BOOK == event.getEventType()) {
            activity = new SaveBookAction().perform(event);
        } else {
            activity = Activities.createErrorActivity(new StorageException("Unsupported event type: " + event.getEventType()));
        }
        return new StorageActivityMessage(activity);
    }

    private class GetBooksAction extends AbstractAction {
        @Override
        public Activity doPerform(Event event) throws Throwable {
            List<Book> books = entityService.getBooks(((BookEvents.GetBooks) event).searchRequest);
            return BookActivities.createGetBooksActivity(books);
        }
    }

    private class SaveBookAction extends AbstractAction {
        @Override
        public Activity doPerform(Event event) throws Throwable {
            Book persistedBook = entityService.saveBook(((BookEvents.SaveBook) event).book);
            return BookActivities.createSaveBookActivity(new BookDto(persistedBook));
        }
    }
}