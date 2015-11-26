package library.storage.akka.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import library.core.akka.AkkaService;
import library.core.eaa.Activities;
import library.core.eaa.Activity;
import library.engine.message.EventMessage;
import library.storage.akka.StorageAkkaConfiguration;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.akka.message.StorageEventMessage;
import library.storage.eaa.BookEvents;
import library.storage.eaa.StorageActivityType;
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
import scala.concurrent.duration.Duration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author Alexander Kuleshov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class StorageSupervisorTest {

    @Autowired
    private AkkaService akkaService;

    private ActorRef storageSupervisor;

    @Configuration
    @Import(StorageAkkaConfiguration.class)
    static class Config {

        @Bean(name = "storageRouter")
        @Scope("prototype")
        public UntypedActor getStorageRouter() {
            return new StorageRouterMock();
        }

        @Bean(name = "storageMonitoringActor")
        @Scope("prototype")
        public UntypedActor getStorageMonitoringActor() {
            return new StorageMonitoringActor();
        }

        @Bean(name = "storageSupervisor")
        @Scope("prototype")
        public UntypedActor getStorageSupervisor() {
            return new StorageSupervisor();
        }

        private class StorageRouterMock extends UntypedActor {
            @Override
            public void onReceive(Object message) throws Exception {
                if (message instanceof StorageEventMessage) {
                    sender().tell(new StorageActivityMessage(((StorageEventMessage) message).id, Activities.createAckActivity()), ((StorageEventMessage) message).origin);
                } else {
                    unhandled(message);
                }


            }
        }
    }

    @Before
    public void init() {
        storageSupervisor = akkaService.getActor("storageSupervisor");
    }

    @Test
    public void highLoadInvocationStorageSupervisor() throws Exception {
        ExecutorService executorService = Executors.newScheduledThreadPool(10);

        Set<Callable<String>> tasks = new HashSet<>();
        for (int i = 0; i < 15; i++) {
            tasks.add(new Callable<String>() {
                public String call() {
                    scala.concurrent.Future<Object> future = Patterns.ask(storageSupervisor,
                            new EventMessage(BookEvents.createGetBookEvent(0L)), Timeout.apply(100, TimeUnit.MILLISECONDS));
                    Object result;
                    try {
                        result = Await.result(future, Duration.Inf());
                    } catch (Exception e) {
                        return "Asynchronous error: " + e.getMessage();
                    }
                    return "Asynchronous task: " + result;
                }
            });
        }

        List<Future<String>> results = executorService.invokeAll(tasks);

        for (Future<String> result : results) {
            System.out.println("future.get = " + result.get());
        }

        executorService.shutdown();
        final boolean done = executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

}
