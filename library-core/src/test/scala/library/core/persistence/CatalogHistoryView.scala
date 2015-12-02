package library.core.persistence

import akka.actor.Props
import akka.persistence.PersistentView

/**
  * @author Alexander Kuleshov
  */
object CatalogHistoryView {
  import CatalogProtocol._
  import CatalogActor._

  case class CatalogHistoryState(authorsHistory: Seq[Author] = Seq.empty) {
    def updated(evt: CatalogEvt): CatalogHistoryState = evt match {
      case CatalogCreated(_) => this
      case AuthorAdded(firstname, lastname) => copy(Author(firstname, lastname) +: authorsHistory)
      case AuthorRemoved(firstname, lastname) => copy(authorsHistory.filterNot(author => author != Author(firstname, lastname)))
    }
  }

  case object GetCatalogHistory

  def props(id: String) = Props(new CatalogHistoryView(id))
}

class CatalogHistoryView(id: String) extends PersistentView {
  import CatalogHistoryView._
  import CatalogProtocol._

  override def persistenceId: String = "catalog." + id
  override def viewId: String = "catalog.view." + id

  var state = CatalogHistoryState()

  val receive: Receive = {
    case evt: CatalogEvt if isPersistent => state = state.updated(evt)
    case GetCatalogHistory => sender ! state
  }
}
