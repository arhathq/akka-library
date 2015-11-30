package library.core.faulttolerance;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.typesafe.config.ConfigFactory;
import library.core.AkkaConfiguration;
import library.core.akka.ActorSystemFactoryBean;
import library.core.akka.AkkaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import scala.concurrent.duration.Duration;

import static library.core.faulttolerance.WorkerApi.Start;

/**
 * @author Alexander Kuleshov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FaultToleranceTestCase {
    @Autowired
    private AkkaService akkaService;

    @Configuration
    @Import(AkkaConfiguration.class)
    static class Config {

        @Bean
        public ActorSystemFactoryBean getAkkaSystemFactoryBean() {
            ActorSystemFactoryBean factoryBean = new ActorSystemFactoryBean();
            factoryBean.setName("FaultToleranceSample");
            factoryBean.setConfig(ConfigFactory.parseString(
                    "akka.loglevel = \"DEBUG\"\n" +
                            "akka.actor.debug {\n" +
                            " receive = on\n" +
                            " lifecycle = on\n" +
                            "}\n"));
            return factoryBean;
        }

        @Bean(name = "workerActor")
        @Scope("prototype")
        public UntypedActor getWorkerActor() {
            return new WorkerActor();
        }

        @Bean(name = "listenerActor")
        @Scope("prototype")
        public UntypedActor getListenerActor() {
            return new ListenerActor();
        }

        @Bean(name = "serviceActor")
        @Scope("prototype")
        public AbstractLoggingActor getServiceActor() {
            return new ServiceActor();
        }

        @Bean(name = "storageActor")
        @Scope("prototype")
        public AbstractLoggingActor getStorageActor() {
            return new StorageActor();
        }
    }

    @Test
    public void testFaultTolerance() {
        ActorRef worker = akkaService.getActor("workerActor");
        ActorRef listener = akkaService.getActor("listenerActor");

        // start the work and listen on progress
        // note that the listener is used as sender of the tell,
        // i.e. it will receive replies from the worker
        akkaService.sendMessage(Start, worker, listener);
        akkaService.getSystem().awaitTermination(Duration.apply(300, "seconds"));
    }
}
