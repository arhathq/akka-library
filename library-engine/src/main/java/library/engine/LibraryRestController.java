package library.engine;

import library.core.web.AbstractRestController;
import library.domain.Author;
import library.domain.Book;
import library.domain.BookSearchRequest;
import library.domain.Publisher;
import library.domain.pojo.BookPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

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

    @RequestMapping(value = "/books", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<List<Book>>> getBooks(BookSearchRequest request) {
        return objectResult(libraryService.getBooks(request));
    }

    @RequestMapping(value = "/book/{id}", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<Book>> getBook(@PathVariable Long id) {
        return objectResult(libraryService.getBook(id));
    }

    @RequestMapping(value = "/book", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity<Long>> saveBook(@RequestBody BookPojo book) {//todo: implement validation
        return objectResult(libraryService.saveBook(book));
    }

    @RequestMapping(value = "/authors", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<List<Author>>> getAuthors() {
        return objectResult(libraryService.getAuthors());
    }

    @RequestMapping(value = "/author/{id}", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<Author>> getAuthor(@PathVariable Long id) {
        return objectResult(libraryService.getAuthor(id));
    }

    @RequestMapping(value = "/author}", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity<Long>> saveAuthor(@RequestBody Author author) {//todo: implement validation
        return objectResult(libraryService.saveAuthor(author));
    }

    @RequestMapping(value = "/publishers", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<List<Publisher>>> getPublishers() {
        return objectResult(libraryService.getPublishers());
    }

    @RequestMapping(value = "/publisher/{id}", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<Publisher>> getPublisher(@PathVariable Long id) {
        return objectResult(libraryService.getPublisher(id));
    }

    @RequestMapping(value = "/publisher", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity<Long>> savePublisher(@RequestBody Publisher publisher) {//todo: implement validation
        return objectResult(libraryService.savePublisher(publisher));
    }

    @RequestMapping(value = "/booksmap", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<Map<String, List<Book>>>> getBooks() {
        return mapResult("books", libraryService.getBooks(new BookSearchRequest()));
    }

}