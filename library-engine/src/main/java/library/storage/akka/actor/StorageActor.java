package library.storage.akka.actor;

import akka.actor.UntypedActor;
import library.storage.StorageEntityService;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.akka.message.StorageEventMessage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Alexander Kuleshov
 */
public abstract class StorageActor extends UntypedActor {

    @Autowired
    protected StorageEntityService entityService;

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof StorageEventMessage) {
            StorageEventMessage eventMessage = (StorageEventMessage) o;
            StorageActivityMessage activityMessage = performActivityByEventDecision(eventMessage);
            eventMessage.origin.tell(activityMessage, self());
        } else {
            unhandled(o);
        }
    }

    protected abstract StorageActivityMessage performActivityByEventDecision(StorageEventMessage eventMessage);
}
