package library.engine;

import library.domain.Author;
import library.domain.Publisher;
import library.domain.pojo.BookPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import library.domain.Book;
import library.domain.BookSearchRequest;
import library.core.web.AbstractRestController;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Kuleshov
 */
@Controller
@RequestMapping(value = "/library", produces = "application/json")
public class LibraryRestController extends AbstractRestController {

    @Autowired
    private LibraryService libraryService;

    @RequestMapping(value = "/book", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<List<Book>>> getBooks(BookSearchRequest request) {
        return objectResult((ListenableFuture<List<Book>>) libraryService.getBooks(request));
    }

    @RequestMapping(value = "/book", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity<Long>> saveBook(@RequestBody BookPojo book) {
        return objectResult((ListenableFuture<Long>) libraryService.saveBook(book));
    }

    @RequestMapping(value = "/authors", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<List<Author>>> getAuthors() {
        return objectResult((ListenableFuture<List<Author>>) libraryService.getAuthors());
    }

    @RequestMapping(value = "/publishers", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<List<Publisher>>> getPublishers() {
        return objectResult((ListenableFuture<List<Publisher>>) libraryService.getPublishers());
    }

    @RequestMapping(value = "/booksmap", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<Map<String, List<Book>>>> getBooks() {
        return mapResult(null, (ListenableFuture<List<Book>>) libraryService.getBooks(null));
    }

}
