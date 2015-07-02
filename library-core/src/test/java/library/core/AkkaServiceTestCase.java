package library.core;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
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
    }

    private static class TestActor extends UntypedActor {

        @Autowired
        private AkkaService akkaService;

        @Override
        public void onReceive(Object o) throws Exception {
            sender().tell(self().path(), self());
        }
    }

    @Test
    public void concurrentTest() {
        final ActorRef originTestActor = akkaService.getSpringActor("testActor");
        for (int i = 0; i < 10; i++) {
            final int id = i;
            new Runnable() {
                @Override
                public void run() {
                    try {
                        ActorRef testActor = akkaService.getSpringActor("testActor");
                        Future<Object> future = Patterns.ask(testActor, String.valueOf(id), 10000);
                        Object result = Await.result(future, Duration.Inf());
                        assertTrue(originTestActor.path().equals(result));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }.run();
        }
    }
}
