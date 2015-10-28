package library.core.akka;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.routing.RoundRobinPool;
import akka.routing.RouterConfig;
import akka.util.Timeout;
import library.core.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ActorSystem system;

    @Resource(name = "routerProps")
    private Properties routerProps;

    private static final Map<String, ActorRef> akkaActors = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    public ActorSystem getSystem() {
        return system;
    }

    public ActorRef getActor(String beanId) {

            ActorRef result = akkaActors.get(beanId);
            if (result == null) {
                try {
                    lock.lock();
                    result = akkaActors.get(beanId);
                    if (result == null) {
                        Props props = SpringExtension.SpringExtProvider.get(system).props(beanId);
                        if (routerProps.containsKey(beanId)) {
                            RouterConfig router = new RoundRobinPool(Integer.parseInt(routerProps.getProperty(beanId)));
                            props = props.withRouter(router);
                        }
                        result = system.actorOf(props, beanId);
                        akkaActors.put(beanId, result);
                    }
                } finally {
                    lock.unlock();
                }
            }
            return result;
    }

    public ActorRef createActor(String beanId) {
        Props props = SpringExtension.SpringExtProvider.get(system).props(beanId);
        return system.actorOf(props);
    }

    public ActorRef createActor(ActorContext context, String beanId) {
        Props props = SpringExtension.SpringExtProvider.get(system).props(beanId);
        return context.actorOf(props);
    }

    public ActorRef createActor(ActorContext context, String beanId, String actorId) {
        Props props = SpringExtension.SpringExtProvider.get(system).props(beanId);
        return context.actorOf(props, actorId);
    }

    public ActorRef createActor(ActorContext context, String beanId, RouterConfig routerConfig) {
        Props props = SpringExtension.SpringExtProvider.get(system).props(beanId).withRouter(routerConfig);
        return context.actorOf(props);
    }

    public void sendMessage(Object message, ActorRef actor) {
        sendMessage(message, actor, ActorRef.noSender());
    }

    public void sendMessage(Object message, ActorRef actor, ActorRef sender) {
        if (actor == null) {
            throw new ApplicationException("Actor is null");
        }
        actor.tell(message, sender);
    }

    public Object sendMessageWithReply(Object message, ActorRef actor, Timeout timeout) {
        return sendMessageWithReply(message, actor, 1, 0, timeout);
    }

    public Object sendMessageWithReply(Object message, ActorRef actor, int maxAttempts, long retryInterval, Timeout timeout) {
        if (actor == null) {
            throw new ApplicationException("Actor is null");
        }

        Exception error = null;
        int attempts = 0;
        while (attempts < maxAttempts) {
            attempts ++;
            try {
                Future future = Patterns.ask(actor, message, timeout);
                Object result = Await.result(future, timeout.duration());
                if (result == null) {
                    throw new ApplicationException("Null was returned by actor [" + actor + "]");
                }
                return result;
            } catch (Exception e) {
                error = e;
                logger.warn("Error sending message [" + message + "] in attemp " + attempts, e);
            }

            if (attempts < maxAttempts) {
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e) {
                    throw new ApplicationException("Thread was interupped", e);
                }
            }
        }

        throw new ApplicationException("Error sending message [" + message + "]", error);
    }

}
