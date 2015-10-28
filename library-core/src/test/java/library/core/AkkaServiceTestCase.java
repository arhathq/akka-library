package library.core;

import akka.actor.ActorRef;
import akka.actor.Status;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
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

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Kuleshov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AkkaServiceTestCase {

    @Autowired
    private AkkaService akkaService;

    @Configuration
    @Import(AkkaConfiguration.class)
    static class Config {

        @Bean(name = "testActor")
        @Scope("prototype")
        public UntypedActor getTestActor() {
            return new TestActor();
        }

        @Bean(name = "statefullActor")
        @Scope("prototype")
        public UntypedActor getStatefullActor() {
            return new StatefullActor();
        }
    }

    private static class TestActor extends UntypedActor {

        @Autowired
        private AkkaService akkaService;

        @Override
        public void onReceive(Object o) throws Exception {
            akkaService.sendMessage(self().path(), sender(), self());
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

    @Test
    public void concurrentTest() {
        final ActorRef originTestActor = akkaService.getActor("testActor");
        for (int i = 0; i < 10; i++) {
            final int id = i;
            new Runnable() {
                @Override
                public void run() {
                    try {
                        ActorRef testActor = akkaService.getActor("testActor");
                        Object result = akkaService.sendMessageWithReply(String.valueOf(id), testActor, Timeout.apply(10000, TimeUnit.MILLISECONDS));
                        assertTrue(originTestActor.path().equals(result));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }.run();
        }
    }

    @Test
    public void sendRetryTest() {
        final ActorRef actor = akkaService.getActor("statefullActor");

        Integer result = (Integer) akkaService.sendMessageWithReply("Hello", actor, 5, 1, Timeout.apply(1, TimeUnit.SECONDS));

        assertTrue(result == 3);
    }
}
