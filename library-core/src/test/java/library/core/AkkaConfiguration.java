package library.core;

import com.typesafe.config.ConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import library.core.akka.ActorSystemFactoryBean;
import library.core.akka.AkkaService;

import java.util.Properties;

/**
 * @author Alexander Kuleshov
 */
@Configuration
public class AkkaConfiguration {
    @Bean
    public ActorSystemFactoryBean getAkkaSystemFactoryBean() {
        ActorSystemFactoryBean factoryBean = new ActorSystemFactoryBean();
        factoryBean.setName("test-akka-framework");
        factoryBean.setConfig(ConfigFactory.parseString(
                "akka.loglevel = DEBUG\n" +
                "akka.actor.debug {\n" +
                "  receive = on\n" +
                "  autoreceive = on\n" +
                "  lifecycle = on\n" +
                "}\n"));
        return factoryBean;
    }

    @Bean
    public AkkaService getAkkaService() {
        return new AkkaService();
    }

    @Bean
    public Properties routerProps() {
        return new Properties();
    }

}
