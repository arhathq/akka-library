package library.engine;

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
    public DeferredResult<ResponseEntity<Long>> saveBook(@RequestBody Book book) {
        return objectResult((ListenableFuture<Long>) libraryService.saveBook(book));
    }

    @RequestMapping(value = "/booksmap", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<Map<String, List<Book>>>> getBooks() {
        return mapResult(null, (ListenableFuture<List<Book>>) libraryService.getBooks(null));
    }

}
