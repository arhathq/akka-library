package library.storage.akka.actor;

import library.core.eaa.*;
import library.domain.Book;
import library.domain.dto.BookDto;
import library.storage.StorageException;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.akka.message.StorageEventMessage;
import library.storage.eaa.BookActivities;
import library.storage.eaa.StorageEventType;

import java.util.List;

import static library.storage.eaa.BookEvents.*;

/**
 * @author Alexander Kuleshov
 */
public class BookActor extends StorageActor {

    @Override
    protected StorageActivityMessage performActivityByEventDecision(StorageEventMessage eventMessage) {
        Event event = eventMessage.event;

        Activity activity;
        if (StorageEventType.GET_BOOKS == event.getEventType()) {
            activity = new GetBooksAction().perform((GetBooks) event);
        } else if (StorageEventType.GET_BOOK == event.getEventType()) {
            activity = new GetBookAction().perform((GetBook) event);
        } else if (StorageEventType.SAVE_BOOK == event.getEventType()) {
            activity = new SaveBookAction().perform((SaveBook) event);
        } else {
            activity = Activities.createErrorActivity(new StorageException("Unsupported event: " + event.getEventType()));
        }
        return createActivityMessage(activity, eventMessage.origin);
    }

    private class GetBooksAction extends ThrowableAction<GetBooks> {
        @Override
        public Activity doPerform(GetBooks event) throws Throwable {
            List<Book> books = entityService.getBooks(event.searchRequest);
            return BookActivities.createGetBooksActivity(books);
        }
    }

    private class GetBookAction extends ThrowableAction<GetBook> {
        @Override
        public Activity doPerform(GetBook event) throws Throwable {
            Book book = entityService.getBook(event.bookId);
            return BookActivities.createGetBookActivity(book);
        }
    }

    private class SaveBookAction extends ThrowableAction<SaveBook> {
        @Override
        public Activity doPerform(SaveBook event) throws Throwable {
            Book persistedBook = entityService.saveBook(event.book);
            return BookActivities.createSaveBookActivity(new BookDto(persistedBook));
        }
    }
}