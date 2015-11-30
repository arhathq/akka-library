package library.core.faulttolerance;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.event.LoggingReceive;
import akka.japi.pf.ReceiveBuilder;

import static library.core.faulttolerance.CounterApi.*;
import static library.core.faulttolerance.CounterServiceApi.*;
import static library.core.faulttolerance.StorageApi.*;


/**
 * The in memory count variable that will send current value to the Storage,
 * if there is any storage available at the moment.
 *
 * @author Alexander Kuleshov
 */
public class CounterActor extends AbstractLoggingActor {

    final String key;
    long count;
    ActorRef storage;

    public CounterActor(String key, long initialValue) {
        this.key = key;
        this.count = initialValue;
        receive(LoggingReceive.create(ReceiveBuilder.
                match(UseStorage.class, useStorage -> {
                    storage = useStorage.storage;
                    storeCount();
                }).
                match(Increment.class, increment -> {
                    count += increment.n;
                    storeCount();
                }).
                matchEquals(GetCurrentCount, gcc -> {
                    sender().tell(new CurrentCount(key, count), self());
                }).build(), context())
        );
    }

    void storeCount() {
        // Delegate dangerous work, to protect our valuable state.
        // We can continue without storage.
        if (storage != null) {
            storage.tell(new Store(new Entry(key, count)), self());
        }
    }
}
