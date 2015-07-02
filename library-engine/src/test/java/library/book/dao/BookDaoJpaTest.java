package library.book.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import library.domain.Book;
import library.book.dao.BookEntity;
import library.book.dao.BookDao;

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
public class BookDaoJpaTest {

    @Autowired
    private BookDao bookDao;

    BookEntity tomSawer, learnErlang;

    @Before
    public void setUp() throws Exception {

        tomSawer = new BookEntity();
        tomSawer.setTitle("Tom Sawer");
        tomSawer.setAuthor("Mark Twaine");
        bookDao.save(tomSawer);

        learnErlang = new BookEntity();
        learnErlang.setTitle("Learn You Some Erlang For Great Good!");
        learnErlang.setAuthor("Fred Hebert");
        bookDao.save(learnErlang);
    }

    @After
    public void tearDown() throws Exception {
        bookDao.deleteAll();
    }

    @Test
    public void findBooks() throws Exception {
        int count = 0;
        for (Book ignored : bookDao.findAll()) {
            count++;
        }
        assertTrue(count == 2);
    }

    @Test
    public void findBook() throws Exception {
        Book book = bookDao.findOne(1L);
        assertNotNull(book);
        assertEquals(tomSawer, book);
    }

    @Test
    public void findBooksById() throws Exception {
        List<Long> ids = new ArrayList<>();
        ids.add(tomSawer.getId());
        ids.add(learnErlang.getId());

        List<BookEntity> books = bookDao.findAll(ids);
        assertTrue(books.size() == ids.size());
    }
}