package library.core.persistence

import akka.persistence.{PersistentActor, SnapshotOffer}

/**
  * @author Alexander Kuleshov
  */
class SnapshottigCounterActor extends PersistentActor {
  import CounterProtocol._

  override def persistenceId: String = "snapshotting-counter"

  var state = 0

  override def receiveCommand: Receive = {
    case Increment => persist(Incremented) { evt =>
      state += 1
      println("Persisted")
    }
    case "takesnapshot" => saveSnapshot(state)
  }

  override def receiveRecover: Receive = {
    case Increment => state += 1
    case SnapshotOffer(_, snapshotState: Int) => state = snapshotState
  }
}



