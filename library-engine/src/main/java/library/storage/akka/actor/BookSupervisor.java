package library.storage.akka.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;
import akka.routing.RouterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import library.core.akka.AkkaService;
import library.storage.akka.message.BookEventMessage;
import library.core.eaa.EventMessage;

/**
 * @author Alexander Kuleshov
 */
public class BookSupervisor extends UntypedActor {
    @Autowired
    private AkkaService akkaService;

    private RouterConfig routerConfig;

    @Override
    public void preStart() throws Exception {
        routerConfig = new RoundRobinPool(10);
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof EventMessage) {
            getBooks((EventMessage) o);
        } else {
            unhandled(o);
        }
    }

    public void getBooks(EventMessage request) {
        ActorRef actorProcessor = akkaService.createSpringActor(context(), "bookActor", routerConfig);
        actorProcessor.tell(new BookEventMessage(request.event, sender()), self());
    }

}
