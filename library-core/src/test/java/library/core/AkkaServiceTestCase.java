package library.core;

import akka.ConfigurationException;
import akka.actor.*;
import akka.pattern.Patterns;
import akka.util.Timeout;
import library.core.akka.ActorSystemFactoryBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import library.core.akka.AkkaService;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author Alexander Kuleshov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AkkaServiceTestCase {

    public static final String TEST_ACTOR = "parentActor";
    public static final String ROUTER_ACTOR = "router";
    public static final String GUARDIAN_SUPERVISOR_NAME = "user";

    private static final Timeout DEFAULT_TIMEOUT = Timeout.apply(1, TimeUnit.SECONDS);

    @Autowired
    private AkkaService akkaService;

    @Configuration
    @Import(AkkaConfiguration.class)
    static class Config {

        @Bean(name = "parentActor")
        @Scope("prototype")
        public UntypedActor getTestActor() {
            return new TestActor();
        }

        @Bean(name = "router")
        @Scope("prototype")
        public UntypedActor getTestRouter() {
            return new TestRouter();
        }

        @Bean(name = "statefullActor")
        @Scope("prototype")
        public UntypedActor getStatefullActor() {
            return new StatefullActor();
        }

        @Bean
        public Properties routerProps() {
            Properties properties = new Properties();
            properties.put(ROUTER_ACTOR, "3");
            return properties;
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetActorSuccess() {
        final ActorRef actor = akkaService.getActor(TEST_ACTOR);
        assertNotNull(actor);
        akkaService.killActor(actor);
    }

    @Test
    public void testGetActorFailure() {
        try {
            akkaService.getActor("testActor1");
            fail("No exception was thrown");
        } catch (Exception e) {
            assertTrue(e instanceof ConfigurationException);
        }
    }

    @Test
    public void testConcurrentGetActor() {
        final ActorRef originTestActor = akkaService.getActor(TEST_ACTOR);
        for (int i = 0; i < 10; i++) {
            ((Runnable) () -> {
                try {
                    ActorRef testActor = akkaService.getActor(TEST_ACTOR);
                    Object result = akkaService.sendMessageWithReply(testActor.path(), originTestActor, DEFAULT_TIMEOUT);
                    assertTrue((Boolean) result);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).run();
        }
        akkaService.killActor(originTestActor);
    }

    @Test
    public void testCreateActors() {
        final ActorRef testActor1 = akkaService.createActor(TEST_ACTOR);
        String parentName1 = akkaService.sendMessageWithReply("getParentName", testActor1, DEFAULT_TIMEOUT);
        assertTrue(GUARDIAN_SUPERVISOR_NAME.equals(parentName1));
        String name1 = akkaService.sendMessageWithReply("getName", testActor1, DEFAULT_TIMEOUT);

        final ActorRef testActor2 = akkaService.createActor(TEST_ACTOR, "actor1");
        String parentName2 = akkaService.sendMessageWithReply("getParentName", testActor2, DEFAULT_TIMEOUT);
        assertTrue(GUARDIAN_SUPERVISOR_NAME.equals(parentName2));
        String name2 = akkaService.sendMessageWithReply("getName", testActor2, DEFAULT_TIMEOUT);

        assertNotEquals(name1, name2);

        try {
            akkaService.createActor(TEST_ACTOR, "actor1");
            fail("No exception was thrown");
        } catch (Exception e) {
            assertTrue(e instanceof InvalidActorNameException);
        }

        akkaService.killActor(testActor1);
        akkaService.killActor(testActor2);
    }

    @Test
    public void testCreateChildActors() {
        final ActorRef testActor = akkaService.getActor(TEST_ACTOR);
        final ActorContext actorContext = akkaService.sendMessageWithReply("getContext", testActor, DEFAULT_TIMEOUT);
        assertNotNull(actorContext);

        final ActorRef childActor = akkaService.createActor(actorContext, TEST_ACTOR, "childActor");
        String name = akkaService.sendMessageWithReply("getName", childActor, DEFAULT_TIMEOUT);
        assertEquals("childActor", name);

        String parentName = akkaService.sendMessageWithReply("getParentName", childActor, DEFAULT_TIMEOUT);
        assertEquals(TEST_ACTOR, parentName);

        ActorRef childActor1 = akkaService.createActor(actorContext, TEST_ACTOR, "childActor1");
        try {
            akkaService.createActor(actorContext, TEST_ACTOR, "childActor1");
            fail("No exception was thrown");
        } catch (Exception e) {
            assertTrue(e instanceof InvalidActorNameException);
        }

        akkaService.killActor(testActor);
        akkaService.killActor(childActor);
        akkaService.killActor(childActor1);
    }

    @Test
    public void testCreateActorWithRouter() {
        final ActorRef testActor = akkaService.createActor(ROUTER_ACTOR);
        akkaService.killActor(testActor);
    }

    @Test
    public void testSendFutureMessage() throws Exception {
        final ActorRef actor = akkaService.createActor(TEST_ACTOR, TEST_ACTOR);
        Future<String> future = akkaService.sendMessage("getName", actor, DEFAULT_TIMEOUT);
        String result = Await.result(future, DEFAULT_TIMEOUT.duration());
        assertEquals(result, TEST_ACTOR);
        akkaService.killActor(actor);
    }

    @Test
    public void sendRetryTest() {
        final ActorRef actor = akkaService.getActor("statefullActor");
        Integer result = akkaService.sendMessageWithReply("Hello", actor, 5, 1, DEFAULT_TIMEOUT);
        assertTrue(result == 3);
        akkaService.killActor(actor);
    }

    private static class TestActor extends UntypedActor {
        @Autowired
        private AkkaService akkaService;

        @Override
        public void onReceive(Object o) throws Exception {
            if (o instanceof ActorPath) {
                ActorPath incomingPath = (ActorPath) o;
                ActorPath selfPath = self().path();
                akkaService.sendMessage(incomingPath.equals(selfPath), sender(), self());
            } else if (o.equals("getParentName")) {
                ActorPath parentPath = context().parent().path();
                akkaService.sendMessage(parentPath.name(), sender(), self());
            } else if (o.equals("getName")) {
                ActorPath selfPath = self().path();
                akkaService.sendMessage(selfPath.name(), sender(), self());
            } else if (o.equals("getContext")) {
                akkaService.sendMessage(context(), sender(), self());
            } else {
                unhandled(o);
            }
        }
    }

    private static class TestRouter extends TestActor {
        @Override
        public void onReceive(Object o) throws Exception {
            super.onReceive(o);
        }
    }

    private static class StatefullActor extends UntypedActor {

        private int count;

        @Autowired
        private AkkaService akkaService;

        @Override
        public void onReceive(Object o) throws Exception {
            count ++;

            if (count == 3) {
                akkaService.sendMessage(count, sender(), self());
            } else {
                akkaService.sendMessage(new Status.Failure(new RuntimeException("Failed")), sender(), self());
            }
        }
    }
}
