package library.core.faulttolerance;

import akka.actor.AbstractLoggingActor;
import akka.event.LoggingReceive;
import akka.japi.pf.ReceiveBuilder;
import library.core.akka.AkkaService;
import org.springframework.beans.factory.annotation.Autowired;

import static library.core.faulttolerance.StorageApi.*;

/**
 * Saves key/value pairs to persistent storage when receiving Store message.
 * Replies with current value when receiving Get message. Will throw
 * StorageException if the underlying data store is out of order.
 *
 * @author Alexander Kuleshov
 */
public class StorageActor extends AbstractLoggingActor {

    final DummyDB db = DummyDB.instance;

    @Autowired
    private AkkaService akkaService;

    public StorageActor() {
        receive(LoggingReceive.create(ReceiveBuilder.
                match(Store.class, store -> {
                    db.save(store.entry.key, store.entry.value);
                }).
                match(Get.class, get -> {
                    Long value = db.load(get.key);
                    akkaService.sendMessage(new Entry(get.key, value == null ? Long.valueOf(0L) : value), sender(), self());
                }).build(), context())
        );
    }
}