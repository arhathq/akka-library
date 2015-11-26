package library.storage.akka.actor;

import akka.actor.UntypedActor;
import library.storage.akka.message.StorageMonitoringMessage;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Alexander Kuleshov
 */
public class StorageMonitoringActor extends UntypedActor {

    private AtomicInteger successCounter = new AtomicInteger(0);
    private AtomicInteger errorCounter = new AtomicInteger(0);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof StorageMonitoringMessage) {

        } else {
            unhandled(message);
        }
    }
}
