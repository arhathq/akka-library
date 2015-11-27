package library.core.faulttolerance;

import akka.actor.UntypedActor;
import library.core.AkkaConfiguration;
import library.core.akka.AkkaService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Properties;

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

        @Bean(name = "worker")
        @Scope("prototype")
        public UntypedActor getWorkerActor() {
            return new WorkerActor();
        }

        @Bean(name = "listener")
        @Scope("prototype")
        public UntypedActor getListenerActor() {
            return new ListenerActor();
        }

        @Bean
        public Properties routerProps() {
            Properties properties = new Properties();
            return properties;
        }
    }

}
