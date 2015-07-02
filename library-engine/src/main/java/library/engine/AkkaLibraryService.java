package library.engine;

import akka.actor.ActorRef;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.SettableListenableFuture;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import library.domain.Book;
import library.domain.BookSearchRequest;
import library.book.akka.*;
import library.core.akka.AkkaService;
import library.book.akka.message.BookActivityMessage;
import library.core.eaa.Event;
import library.core.eaa.EventMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexander Kuleshov
 */
@Service
public class AkkaLibraryService implements LibraryService {

    @Autowired
    private AkkaService akkaService;

    @Override
    public Future<List<Book>> getBooks(final BookSearchRequest request) {
        ActorRef bookSupervisor = akkaService.getSpringActor("bookSupervisor");
        Event bookEvent = BookEvents.createGetBooksEvent(request);
        Timeout timeout = new Timeout(Duration.create(2, TimeUnit.SECONDS));
        scala.concurrent.Future<Object> future = Patterns.ask(bookSupervisor, new EventMessage(bookEvent), timeout);

        final SettableListenableFuture<List<Book>> result = new SettableListenableFuture<>();
        ExecutionContext executionContext = akkaService.getSystem().dispatcher();
        future.onSuccess(new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object o) throws Throwable {
                BookActivityMessage message = (BookActivityMessage) o;
                List<Book> books = new ArrayList<>();
                books.addAll((Collection<? extends Book>) message.activity.getData());
                result.set(books);

            }
        }, executionContext);

        future.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable throwable) throws Throwable {
                result.setException(throwable);
            }
        }, executionContext);

        return result;
    }

    @Override
    public Future<Long> saveBook(final Book book) {
        ActorRef bookSupervisor = akkaService.getSpringActor("bookSupervisor");
        Event bookEvent = BookEvents.createSaveBookEvent(book);
        Timeout timeout = new Timeout(Duration.create(2, TimeUnit.SECONDS));
        scala.concurrent.Future<Object> future = Patterns.ask(bookSupervisor, new EventMessage(bookEvent), timeout);

        final SettableListenableFuture<Long> result = new SettableListenableFuture<>();
        ExecutionContext executionContext = akkaService.getSystem().dispatcher();
        future.onSuccess(new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object o) throws Throwable {
                BookActivityMessage message = (BookActivityMessage) o;
                result.set(((Book) message.activity.getData()).getId());
            }
        }, executionContext);

        future.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable throwable) throws Throwable {
                result.setException(throwable);
            }
        }, executionContext);

        return result;
    }

}