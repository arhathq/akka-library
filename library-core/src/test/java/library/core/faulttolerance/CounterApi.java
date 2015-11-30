package library.core.faulttolerance;

import akka.actor.ActorRef;

/**
 * @author Alexander Kuleshov
 */
public interface CounterApi {
    public static class UseStorage {
        public final ActorRef storage;
        public UseStorage(ActorRef storage) {
            this.storage = storage;
        }
        public String toString() {
            return String.format("%s(%s)", getClass().getSimpleName(), storage);
        }
    }
}
