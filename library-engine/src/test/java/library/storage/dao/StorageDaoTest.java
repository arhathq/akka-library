package library.storage.dao;

import library.storage.entity.AuthorEntity;
import library.storage.entity.BookEntity;
import library.storage.entity.PublisherEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import library.domain.Book;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Kuleshov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/testContext-dao.xml")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StorageDaoTest {

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private BookDao bookDao;

    @Autowired
    private PublisherDao publisherDao;

    AuthorEntity markTwain, fredHebert;
    BookEntity tomSawer, learnErlang;
    PublisherEntity oreili;

    @Before
    public void setUp() throws Exception {

        oreili = new PublisherEntity();
        oreili.setName("O'Reili");
        oreili = publisherDao.save(oreili);

        assertTrue(oreili.getId() != null);

        markTwain = new AuthorEntity();
        markTwain.setFirstName("Mark");
        markTwain.setLastName("Twain");
        markTwain = authorDao.save(markTwain);

        assertTrue(markTwain.getId() != null);

        tomSawer = new BookEntity();
        tomSawer.setTitle("Tom Sawer");
        tomSawer.getAuthors().add(markTwain);
        tomSawer.setPublisher(oreili);
        tomSawer = bookDao.save(tomSawer);

        assertTrue(tomSawer.getId() != null);

        fredHebert = new AuthorEntity();
        fredHebert.setFirstName("Fred");
        fredHebert.setLastName("Hebert");
        fredHebert = authorDao.save(fredHebert);

        learnErlang = new BookEntity();
        learnErlang.setTitle("Learn You Some Erlang For Great Good!");
        learnErlang.getAuthors().add(fredHebert);
        learnErlang.setPublisher(oreili);
        learnErlang = bookDao.save(learnErlang);
    }

    @After
    public void tearDown() throws Exception {
        bookDao.delete(tomSawer);
        bookDao.delete(learnErlang);
        authorDao.delete(markTwain);
        authorDao.delete(fredHebert);
        publisherDao.delete(oreili);

        assertTrue(bookDao.findAll().isEmpty());
        assertTrue(authorDao.findAll().isEmpty());
        assertTrue(publisherDao.findAll().isEmpty());
    }

    @Test
    public void findBooks() throws Exception {
        List<BookEntity> books = bookDao.findAll();

        assertTrue(books.size() == 2);
        assertTrue(books.contains(tomSawer));
        assertTrue(books.contains(learnErlang));
    }

    @Test
    public void findBook() throws Exception {
        Book book = bookDao.findOne(tomSawer.getId());

        assertNotNull(book);
        assertEquals(tomSawer, book);

        assertEquals(tomSawer.getPublisher(), oreili);
    }

    @Test
    public void findBooksById() throws Exception {
        List<Long> ids = new ArrayList<>();
        ids.add(tomSawer.getId());
        ids.add(learnErlang.getId());

        List<BookEntity> books = bookDao.findAll(ids);

        assertTrue(books.size() == ids.size());
        assertTrue(books.contains(tomSawer));
        assertTrue(books.contains(learnErlang));
    }

    @Test
    public void concurrentUpdateBook() throws Exception {
        BookEntity aliceBook = bookDao.findOne(tomSawer.getId());
        int aliceVersion = aliceBook.getVersion();

        BookEntity batchBook = bookDao.findOne(tomSawer.getId());
        int batchVersion = batchBook.getVersion();

        assertTrue(aliceVersion == batchVersion);

        batchBook.setPublisher(null);
        batchBook = bookDao.save(batchBook);
        batchVersion = batchBook.getVersion();

        assertTrue(aliceVersion < batchVersion);

        aliceBook.setTitle("updated title");
        try {
            aliceBook = bookDao.save(aliceBook);
        } catch (OptimisticLockingFailureException e) {
            BookEntity latestBook = bookDao.findOne(aliceBook.getId());
            aliceBook = bookDao.save(latestBook.from(aliceBook));
        }
        aliceVersion = aliceBook.getVersion();

        assertTrue(aliceVersion > batchVersion);

        tomSawer = bookDao.findOne(tomSawer.getId()); //Update with last modified version
    }
}