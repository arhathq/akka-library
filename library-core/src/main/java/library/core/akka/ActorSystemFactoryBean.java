package library.core.akka;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Alexander Kuleshov
 */
public class ActorSystemFactoryBean implements FactoryBean<ActorSystem>, ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String name;

    private ApplicationContext applicationContext;

    private ActorSystem system;

    @PostConstruct
    public void init() {
        logger.info("Starting Akka ActorSystem");
        Config config = ConfigFactory.load(getClass().getClassLoader());
        system = ActorSystem.create(name, config);
        SpringExtension.SpringExtProvider.get(system).initialize(applicationContext);
    }

    @PreDestroy
    public void close() {
        logger.info("Shutdown Akka ActorSystem");
        system.shutdown();
        system.awaitTermination();
    }

    @Required
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public ActorSystem getObject() throws Exception {
        return system;
    }

    @Override
    public Class<?> getObjectType() {
        return ActorSystem.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
