package library.core.persistence

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

import akka.actor.Props
import akka.pattern.ask
import akka.pattern.pipe
import akka.persistence.{SnapshotOffer, PersistentActor}
import akka.util.Timeout

/**
  * @author Alexander Kuleshov
  */
object CatalogActor {
  import CatalogProtocol._

  sealed trait CatalogCommand
  case class CreateCatalog(id: String) extends CatalogCommand
  case class AddAuthor(firstname: String, lastname: String) extends CatalogCommand
  case class RemoveAuthor(id: Int) extends CatalogCommand

  sealed trait CatalogQuery
  case class GetAuthors() extends CatalogQuery

  case class CatalogState(id: String, authors: Seq[Author] = Nil) {
    def updated(evt: CatalogEvt): CatalogState = evt match {
      case AuthorAdded(author) => copy(id, author +: authors)
      case AuthorRemoved(authorId) => copy(id, authors.filterNot(a => a.id.get == authorId))
      case _ => this
    }
  }

  def props(id: String) = Props(new CatalogActor(id))
}

class CatalogActor(id: String) extends PersistentActor {
  import CatalogActor._
  import CatalogProtocol._
  import CounterProtocol._

  override def persistenceId: String = "catalog." + id

  val counterActor = context.actorOf(CounterActor.props())

  var state: Option[CatalogState] = None

  def updateState(evt: CatalogEvt) = state = state.map(_.updated(evt))

  def initialState(evt: CatalogCreated) = {
    state = Some(CatalogState(evt.id, Seq()))
    context.become(receiveCommands)
  }

  val receiveRecover: Receive = {
    case evt: CatalogCreated => initialState(evt)
    case evt: CatalogEvt => updateState(evt)
    case SnapshotOffer(_, snapshot: CatalogState) => {
      state = Some(snapshot)
      context.become(receiveCommands)
    }
  }

  val receiveCreate: Receive = {
    case command@CreateCatalog(id) => {
      persist(CatalogCreated(id)) { evt =>
        println(s"Creating catalog from message $command")
        initialState(evt)
      }
    }
  }

  val receiveCommands: Receive = {
    case AddAuthor(firstname, lastname) => {
      implicit val timeout = Timeout(5 seconds)
      implicit val ec = ExecutionContext.fromExecutor(context.dispatcher)
      val idFuture = counterActor ? Increment
      idFuture.mapTo[Int].
        map(id => Author(Some(id), firstname, lastname)).
        map(author => {
          val event = AuthorAdded(author)
          persist(event) {
            println(s"Author '${author.id.get} ${author.firstname} ${author.lastname}' added to catalog")
            evt => updateState(evt)
          }
          event
        }).
      pipeTo(sender)


    }
    case RemoveAuthor(authorId) => {
      persist(AuthorRemoved(authorId)) {
        println(s"Author '$authorId' removed from catalog")
        evt => updateState(evt)
        sender ! evt
      }
    }
    case GetAuthors => {
      sender ! state.get.authors
    }
  }

  val receiveCommand: Receive = receiveCreate
}