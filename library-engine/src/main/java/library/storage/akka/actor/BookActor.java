package library.storage.akka.actor;

import akka.actor.UntypedActor;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import library.domain.Book;
import library.domain.BookDto;
import library.domain.BookSearchRequest;
import library.core.eaa.Action;
import library.core.eaa.Activity;
import library.core.eaa.Event;
import library.storage.BookEventType;
import library.storage.BookException;
import library.storage.akka.*;
import library.storage.akka.message.BookActivityMessage;
import library.storage.akka.message.BookEventMessage;
import library.book.dao.BookDao;
import library.book.dao.BookEntity;

import java.util.List;

/**
 * @author Alexander Kuleshov
 */
public class BookActor extends UntypedActor {

    @Autowired
    private BookDao bookDao;

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof BookEventMessage) {
            BookEventMessage eventMessage = (BookEventMessage) o;
            BookActivityMessage activityMessage = performActivityByEventDecision(eventMessage);
            eventMessage.origin.tell(activityMessage, self());
        } else {
            unhandled(o);
        }
    }

    private BookActivityMessage performActivityByEventDecision(BookEventMessage eventMessage) {
        Event event = eventMessage.event;

        Action bookAction;
        if (BookEventType.GET_BOOKS == event.getEventType()) {
            bookAction = new GetBooksAction();
        } else if (BookEventType.SAVE_BOOK == event.getEventType()) {
            bookAction = new SaveBookAction();
        } else {
            throw new BookException("Unsupported event type: " + event.getEventType());
        }

        Activity activity = bookAction.perform(event);
        return new BookActivityMessage(activity);
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

            BookEntity persistedBook = bookDao.save(new BookEntity(book));

            return BookActivities.createSaveBookActivity(new BookDto(persistedBook));
        }
    }
}
