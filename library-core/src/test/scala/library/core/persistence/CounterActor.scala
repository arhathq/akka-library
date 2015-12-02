package library.core.persistence

import akka.persistence.PersistentActor

/**
  * @author Alexander Kuleshov
  */
object CounterProtocol {
  case object Increment
  case object Incremented
}

class CounterActor extends PersistentActor {
  import CounterProtocol._

  override def persistenceId: String = "counter"

  var state = 0

  override def receiveCommand: Receive = {
    case Increment => persist(Incremented) { evt =>
      state += 1
      println("Persisted")
    }
  }

  override def receiveRecover: Receive = {
    case Incremented => state += 1
  }
}