package library.storage.akka.actor;

import akka.actor.UntypedActor;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import library.storage.eaa.BookActivities;
import library.storage.eaa.BookEvents;
import org.springframework.beans.factory.annotation.Autowired;
import library.domain.Book;
import library.domain.dto.BookDto;
import library.domain.BookSearchRequest;
import library.core.eaa.Action;
import library.core.eaa.Activity;
import library.core.eaa.Event;
import library.storage.eaa.StorageEventType;
import library.storage.StorageException;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.akka.message.StorageEventMessage;
import library.storage.dao.BookDao;
import library.storage.entity.BookEntity;

import java.util.List;

/**
 * @author Alexander Kuleshov
 */
public class BookActor extends UntypedActor {

    @Autowired
    private BookDao bookDao;

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof StorageEventMessage) {
            StorageEventMessage eventMessage = (StorageEventMessage) o;
            StorageActivityMessage activityMessage = performActivityByEventDecision(eventMessage);
            eventMessage.origin.tell(activityMessage, self());
        } else {
            unhandled(o);
        }
    }

    private StorageActivityMessage performActivityByEventDecision(StorageEventMessage eventMessage) {
        Event event = eventMessage.event;

        Action bookAction;
        if (StorageEventType.GET_BOOKS == event.getEventType()) {
            bookAction = new GetBooksAction();
        } else if (StorageEventType.SAVE_BOOK == event.getEventType()) {
            bookAction = new SaveBookAction();
        } else {
            throw new StorageException("Unsupported event type: " + event.getEventType());
        }

        Activity activity = bookAction.perform(event);
        return new StorageActivityMessage(activity);
    }

    private class GetBooksAction implements Action {

        @Override
        public Activity perform(Event bookEvent) {
            BookEvents.GetBooks event = (BookEvents.GetBooks) bookEvent;
            BookSearchRequest bookSearchRequest = event.searchRequest;

            List<BookEntity> bookEntities = bookDao.findAll(bookSearchRequest);

            List<Book> books = Lists.transform(bookEntities, new Function<BookEntity, Book>() {
                @Override
                public Book apply(BookEntity bookEntity) {
                    return new BookDto(bookEntity);
                }
            });

            return BookActivities.createGetBooksActivity(books);
        }
    }

    private class SaveBookAction implements Action {

        @Override
        public Activity perform(Event bookEvent) {
            BookEvents.SaveBook event = (BookEvents.SaveBook) bookEvent;
            Book book = event.book;

            // TODO: Should be performed in transaction context
            BookEntity persistedBook = bookDao.toEntity(book);
            persistedBook = bookDao.save(persistedBook);

            return BookActivities.createSaveBookActivity(new BookDto(persistedBook));
        }
    }
}
