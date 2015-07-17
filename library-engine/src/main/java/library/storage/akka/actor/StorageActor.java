package library.storage.akka.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import library.core.eaa.Activity;
import library.core.eaa.ThrowableActivity;
import library.storage.StorageEntityService;
import library.storage.akka.message.StorageActivityMessage;
import library.storage.akka.message.StorageErrorMessage;
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

    protected StorageActivityMessage createActivityMessage(Activity activity, ActorRef destination) {
        if (activity instanceof ThrowableActivity) {
            return new StorageErrorMessage((ThrowableActivity) activity, destination);
        }

        return new StorageActivityMessage(activity);
    }
}
