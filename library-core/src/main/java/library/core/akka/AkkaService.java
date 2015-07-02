package library.core.akka;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import akka.routing.RouterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Alexander Kuleshov
 */
@Component
public class AkkaService {

    @Autowired
    private ActorSystem system;

    @Resource(name = "routerProps")
    private Properties routerProps;

    private static final Map<String, ActorRef> springActors = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    public ActorSystem getSystem() {
        return system;
    }

    public ActorRef getSpringActor(String beanId) {

            ActorRef result = springActors.get(beanId);
            if (result == null) {
                try {
                    lock.lock();
                    result = springActors.get(beanId);
                    if (result == null) {
                        Props props = SpringExtension.SpringExtProvider.get(system).props(beanId);
                        if (routerProps.containsKey(beanId)) {
                            RouterConfig router = new RoundRobinPool(Integer.parseInt(routerProps.getProperty(beanId)));
                            props = props.withRouter(router);
                        }
                        result = system.actorOf(props, beanId);
                        springActors.put(beanId, result);
                    }
                } finally {
                    lock.unlock();
                }
            }
            return result;
    }

    public ActorRef createSpringActor(String beanId) {
        Props props = SpringExtension.SpringExtProvider.get(system).props(beanId);
        return system.actorOf(props);
    }

    public ActorRef createSpringActor(ActorContext context, String beanId) {
        Props props = SpringExtension.SpringExtProvider.get(system).props(beanId);
        return context.actorOf(props);
    }

    public ActorRef createSpringActor(ActorContext context, String beanId, String actorId) {
        Props props = SpringExtension.SpringExtProvider.get(system).props(beanId);
        return context.actorOf(props, actorId);
    }

    public ActorRef createSpringActor(ActorContext context, String beanId, RouterConfig routerConfig) {
        Props props = SpringExtension.SpringExtProvider.get(system).props(beanId).withRouter(routerConfig);
        return context.actorOf(props);
    }

}
