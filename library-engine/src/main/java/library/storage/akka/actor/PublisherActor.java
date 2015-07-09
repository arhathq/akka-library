package library.storage.akka.actor;

import library.core.eaa.Activities;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.akka.message.StorageErrorMessage;
import library.storage.akka.message.StorageEventMessage;

/**
 * @author Alexander Kuleshov
 */
public class PublisherActor extends StorageActor {

    @Override
    protected StorageActivityMessage performActivityByEventDecision(StorageEventMessage eventMessage) {
        return new StorageErrorMessage(Activities.createErrorActivity(new UnsupportedOperationException()), eventMessage.origin);
    }
}
