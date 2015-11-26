package library.storage.akka;

import library.core.akka.ActorSystemFactoryBean;
import library.core.akka.AkkaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author Alexander Kuleshov
 */
@Configuration
public class StorageAkkaConfiguration {
    @Bean
    public ActorSystemFactoryBean getAkkaSystemFactoryBean() {
        ActorSystemFactoryBean factoryBean = new ActorSystemFactoryBean();
        factoryBean.setName("storage");
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
