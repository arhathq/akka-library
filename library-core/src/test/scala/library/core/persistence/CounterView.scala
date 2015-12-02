package library.core.persistence

import akka.persistence.PersistentView

/**
  * @author Alexander Kuleshov
  */
class CounterView extends PersistentView {
  import CounterProtocol._

  override def persistenceId: String = "counter"
  override def viewId: String = "counter-view"

  var queryState = 0

  override def receive: Receive = {
    case Incremented if isPersistent => {
      queryState = someVeryComplicatedCalculaton(queryState)
      // Or update a database
    }
    case ComplexQuery => {
      sender ! queryState
      // Or perform specialized query on datastore
    }
  }

  def someVeryComplicatedCalculaton(queryState: Int): Int = ???
}

case object ComplexQuery