package library.core.akka;

import akka.actor.*;
import akka.pattern.Patterns;
import akka.routing.RoundRobinPool;
import akka.routing.RouterConfig;
import akka.util.Timeout;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import library.core.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import scala.reflect.ClassTag;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static akka.japi.Util.classTag;

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

    private final Map<String, ActorRef> parentActors = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    // Metrcis
    private final Meter messagesMeter = AkkaMetrics.newMeter(AkkaService.class, "total-messages");
    private final Counter toplevelActorsCounter = AkkaMetrics.newCounter(AkkaService.class, "toplevel-actors-count");

    public ActorSystem getSystem() {
        return system;
    }

    public ActorRef getActor(String beanId) {
        ActorRef actor = parentActors.get(beanId);
        if (actor == null) {
            try {
                lock.lock();
                actor = parentActors.get(beanId);
                if (actor == null) {
                    actor = createActor(beanId, beanId);
                    parentActors.put(beanId, actor);
                    toplevelActorsCounter.inc();
                }
            } finally {
                lock.unlock();
            }
        }
        return actor;
    }

    public ActorRef createActor(String beanId) {
        return createActor(system, beanId, null, null);
    }

    public ActorRef createActor(String beanId, String actorId) {
        return createActor(system, beanId, actorId, null);
    }

    public ActorRef createActor(String beanId, String actorId, RouterConfig routerConfig) {
        return createActor(system, beanId, actorId, routerConfig);
    }

    public ActorRef createActor(ActorContext context, String beanId) {
        return createActor(context, beanId, null, null);
    }

    public ActorRef createActor(ActorContext context, String beanId, String actorId) {
        return createActor(context, beanId, actorId, null);
    }

    public ActorRef createActor(ActorRefFactory actorRefFactory, String beanId, String actorId, RouterConfig routerConfig) {
        Props props = SpringExtension.SpringExtProvider.get(system).props(beanId);

        if (routerConfig != null) {
            props = props.withRouter(routerConfig);
        } else if (routerProps.containsKey(beanId)) {
            RouterConfig router = new RoundRobinPool(Integer.parseInt(routerProps.getProperty(beanId)));
            props = props.withRouter(router);
        }

        if (actorId != null) {
            return actorRefFactory.actorOf(props, actorId);
        }
        return actorRefFactory.actorOf(props);
    }

    public ActorRef createActor(ActorRefFactory actorRefFactory, Class actorClass,  String actorId, Object... args) {
        Props props = Props.create(actorClass, args);
        return actorRefFactory.actorOf(props, actorId);
    }

    public void stopActor(String beanId) {
        ActorRef actor = parentActors.get(beanId);
        if (actor != null) {
            try {
                lock.lock();
                actor = parentActors.get(beanId);
                if (actor != null) {
                    stopActor(actor);
                    parentActors.remove(beanId);
                    toplevelActorsCounter.dec();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void stopActor(ActorRef actor) {
        actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }

    public void sendMessage(Object message, ActorRef actor) {
        sendMessage(message, actor, ActorRef.noSender());
    }

    public void sendMessage(Object message, ActorRef actor, ActorRef sender) {
        messagesMeter.mark();
        if (actor == null) {
            throw new ApplicationException("Actor is null");
        }
        actor.tell(message, sender);
    }

    public <T> Future<T> sendMessageWithFutureReply(Object message, ActorRef actor, Timeout timeout) {
        messagesMeter.mark();
        if (actor == null) {
            throw new ApplicationException("Actor is null");
        }
        return (Future<T>) Patterns.ask(actor, message, timeout);
    }

    public <T> T sendMessageWithReply(Object message, ActorRef actor, Timeout timeout) {
        return sendMessageWithReply(message, actor, 1, 0, timeout);
    }

    public <T> T sendMessageWithReply(Object message, ActorRef actor, int maxAttempts, long retryInterval, Timeout timeout) {
        if (actor == null) {
            throw new ApplicationException("Actor is null");
        }

        Exception error = null;
        int attempts = 0;
        while (attempts < maxAttempts) {
            attempts ++;
            try {
                Future<T> future = (Future<T>) Patterns.ask(actor, message, timeout);
                T result = Await.result(future, timeout.duration());
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

    public void schedule(ActorContext context, ActorRef receiver, ActorRef sender, Object message, FiniteDuration initialDelay, FiniteDuration interval) {
        context.system().scheduler().schedule(initialDelay, interval, receiver, message, context.dispatcher(), sender);
    }

    public void scheduleOnce(ActorContext context, ActorRef receiver, ActorRef sender, Object message, FiniteDuration delay) {
        context.system().scheduler().scheduleOnce(delay, receiver, message, context.dispatcher(), sender);
    }
}
