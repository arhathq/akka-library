package library.core.faulttolerance;

import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import library.core.akka.AkkaService;
import org.springframework.beans.factory.annotation.Autowired;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * @author Alexander Kuleshov
 */
public class ListenerActor extends UntypedActor {

    private final LoggingAdapter logger = Logging.getLogger(context().system(), this);

    private final Duration timeout = Duration.create(15, TimeUnit.SECONDS);

    @Autowired
    private AkkaService akkaService;

    @Override
    public void preStart() throws Exception {
        // If not any progress within 15 seconds then service is unavailable
        context().setReceiveTimeout(timeout);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        logger.debug("Received message {}", message);
        if (message instanceof WorkerApi.Progress) {
            WorkerApi.Progress progress = (WorkerApi.Progress) message;
            logger.info("Current progress {} %", progress.percent);
            if(progress.percent >= 100.0) {
                logger.info("Progress complete, shutting down.");
                context().system().terminate();
            }
        } else if (ReceiveTimeout.getInstance() != null) {
            // No progress within 15 seconds, ServiceUnavailable
            logger.error("Shutting down due to unavailable service");
            context().system().terminate();
        } else {
            unhandled(message);
        }
    }
}
