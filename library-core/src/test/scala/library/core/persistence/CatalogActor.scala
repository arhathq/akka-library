package library.core.persistence

import akka.actor.Props
import akka.persistence.{SnapshotOffer, PersistentActor}

/**
  * @author Alexander Kuleshov
  */
object CatalogActor {
  import CatalogProtocol._

  sealed trait CatalogCommand
  case class CreateCatalog(id: String) extends CatalogCommand
  case class AddAuthor(firstname: String, lastname: String) extends CatalogCommand
  case class RemoveAuthor(firstname: String, lastname: String) extends CatalogCommand

  sealed trait CatalogQuery
  case class GetAuthors() extends CatalogQuery

  case class CatalogState(id: String, authors: Seq[Author] = Nil) {
    def updated(evt: CatalogEvt): CatalogState = evt match {
      case AuthorAdded(firstname, lastname) => copy(id, Author(firstname, lastname) +: authors)
      case AuthorRemoved(firstname, lastname) => copy(id, authors.filterNot(author => author == Author(firstname, lastname)))
      case _ => this
    }
  }
  case class Author(firstname: String, lastname: String)

  def props(id: String) = Props(new CatalogActor(id))
}

class CatalogActor(id: String) extends PersistentActor {
  import CatalogActor._
  import CatalogProtocol._

  override def persistenceId: String = "catalog." + id

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
      persist(AuthorAdded(firstname, lastname)) {
        println(s"Author '$firstname $lastname' added to catalog")
        evt => updateState(evt)
        sender ! evt
      }
    }
    case RemoveAuthor(firstname, lastname) => {
      persist(AuthorRemoved(firstname, lastname)) {
        println(s"Author '$firstname $lastname' removed from catalog")
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