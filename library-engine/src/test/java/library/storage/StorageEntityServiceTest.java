package library.storage;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

/**
 * @author Alexander Kuleshov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:config/testContext.xml", "classpath:config/testContext-dao.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StorageEntityServiceTest {

    @Autowired
    private StorageEntityService entityService;

    @Test
    public void getService() {
        assertNotNull(entityService);
    }
}
