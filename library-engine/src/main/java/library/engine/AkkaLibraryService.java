package library.engine;

import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import library.core.akka.AkkaService;
import library.core.eaa.Event;
import library.domain.Author;
import library.domain.Book;
import library.domain.BookSearchRequest;
import library.domain.Publisher;
import library.engine.message.ActivityMessage;
import library.engine.message.EventMessage;
import library.storage.eaa.AuthorEvents;
import library.storage.eaa.BookEvents;
import library.storage.eaa.PublisherEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;
import scala.PartialFunction;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexander Kuleshov
 */
@Service
public class AkkaLibraryService implements LibraryService {

    public static final String STORAGE_SUPERVISOR = "storageSupervisor";

    private static final Timeout timeout = new Timeout(Duration.create(2, TimeUnit.SECONDS));

    @Autowired
    private AkkaService akkaService;

    @Override
    public ListenableFuture<List<Book>> getBooks(BookSearchRequest request) {
        final SettableListenableFuture<List<Book>> result = new SettableListenableFuture<>();

        PartialFunction<Object, ?> onSuccess = new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object o) throws Throwable {
                ActivityMessage message = (ActivityMessage) o;
                List<Book> books = new ArrayList<>();
                books.addAll((Collection<? extends Book>) message.activity.getPayload());
                result.set(books);
            }
        };

        sendEvent(BookEvents.createGetBooksEvent(request), onSuccess, onFailure(result));

        return result;
    }

    @Override
    public ListenableFuture<Book> getBook(Long id) throws LibraryException {
        final SettableListenableFuture<Book> result = new SettableListenableFuture<>();

        PartialFunction<Object, ?> onSuccess = new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object o) throws Throwable {
                ActivityMessage message = (ActivityMessage) o;
                result.set((Book) message.activity.getPayload());
            }
        };

        sendEvent(BookEvents.createGetBookEvent(id), onSuccess, onFailure(result));

        return result;
    }

    @Override
    public ListenableFuture<Long> saveBook(Book book) {
        final SettableListenableFuture<Long> result = new SettableListenableFuture<>();

        PartialFunction<Object, ?> onSuccess = new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object o) throws Throwable {
                ActivityMessage message = (ActivityMessage) o;
                result.set(((Book) message.activity.getPayload()).getId());
            }
        };

        sendEvent(BookEvents.createSaveBookEvent(book), onSuccess, onFailure(result));

        return result;
    }

    @Override
    public ListenableFuture<List<Author>> getAuthors() throws LibraryException {
        final SettableListenableFuture<List<Author>> result = new SettableListenableFuture<>();

        PartialFunction<Object, ?> onSuccess = new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object o) throws Throwable {
                ActivityMessage message = (ActivityMessage) o;
                result.set((List<Author>) message.activity.getPayload());

            }
        };

        sendEvent(AuthorEvents.createGetAuthorsEvent(), onSuccess, onFailure(result));

        return result;
    }

    @Override
    public ListenableFuture<Author> getAuthor(Long id) throws LibraryException {
        final SettableListenableFuture<Author> result = new SettableListenableFuture<>();

        PartialFunction<Object, ?> onSuccess = new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object o) throws Throwable {
                ActivityMessage message = (ActivityMessage) o;
                result.set((Author) message.activity.getPayload());
            }
        };

        sendEvent(AuthorEvents.createGetAuthorEvent(id), onSuccess, onFailure(result));

        return result;
    }

    @Override
    public ListenableFuture<Long> saveAuthor(Author author) throws LibraryException {
        final SettableListenableFuture<Long> result = new SettableListenableFuture<>();

        PartialFunction<Object, ?> onSuccess = new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object o) throws Throwable {
                ActivityMessage message = (ActivityMessage) o;
                result.set(((Author) message.activity.getPayload()).getId());
            }
        };

        sendEvent(AuthorEvents.createSaveAuthorEvent(author), onSuccess, onFailure(result));

        return result;
    }

    @Override
    public ListenableFuture<List<Publisher>> getPublishers() throws LibraryException {
        final SettableListenableFuture<List<Publisher>> result = new SettableListenableFuture<>();

        PartialFunction<Object, ?> onSuccess = new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object o) throws Throwable {
                ActivityMessage message = (ActivityMessage) o;
                result.set((List<Publisher>) message.activity.getPayload());

            }
        };

        sendEvent(PublisherEvents.createGetPublishersEvent(), onSuccess, onFailure(result));

        return result;
    }

    @Override
    public ListenableFuture<Publisher> getPublisher(Long id) throws LibraryException {
        final SettableListenableFuture<Publisher> result = new SettableListenableFuture<>();

        PartialFunction<Object, ?> onSuccess = new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object o) throws Throwable {
                ActivityMessage message = (ActivityMessage) o;
                result.set((Publisher) message.activity.getPayload());
            }
        };

        sendEvent(PublisherEvents.createGetPublisherEvent(id), onSuccess, onFailure(result));

        return result;
    }

    @Override
    public ListenableFuture<Long> savePublisher(Publisher publisher) throws LibraryException {
        final SettableListenableFuture<Long> result = new SettableListenableFuture<>();

        PartialFunction<Object, ?> onSuccess = new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object o) throws Throwable {
                ActivityMessage message = (ActivityMessage) o;
                result.set(((Author) message.activity.getPayload()).getId());
            }
        };

        sendEvent(PublisherEvents.createSavePublisherEvent(publisher), onSuccess, onFailure(result));

        return result;
    }

    private void sendEvent(Event event, PartialFunction<Object, ?> onSuccess, PartialFunction<Throwable, ?> onFailure) throws LibraryException {
        scala.concurrent.Future<Object> future =
                Patterns.ask(akkaService.getSpringActor(STORAGE_SUPERVISOR), new EventMessage(event), timeout);
        ExecutionContext executionContext = akkaService.getSystem().dispatcher();
        future.onSuccess(onSuccess, executionContext);
        future.onFailure(onFailure, executionContext);
    }

    private PartialFunction<Throwable, ?> onFailure(final SettableListenableFuture<?> result) {
        return new OnFailure() {
            @Override
            public void onFailure(Throwable throwable) throws Throwable {
                result.setException(throwable);
            }
        };
    }
}