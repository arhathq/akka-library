package library.core.persistence

import akka.persistence.Update
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import akka.actor.ActorSystem
import akka.actor.actorRef2Scala
import akka.pattern.ask
import akka.util.Timeout

/**
  * @author Alexander Kuleshov
  */
object CatalogApp extends App {

  import CatalogActor._
  import CatalogHistoryView._

  val config = ConfigFactory.parseString(
    """
      |akka.loglevel = "DEBUG"
      |
      |akka.persistence.journal.plugin = "akka.persistence.journal.leveldb"
      |akka.persistence.snapshot-store.plugin = "akka.persistence.snapshot-store.local"
      |
      |akka.persistence.journal.leveldb.dir = "target/akka-journal"
      |akka.persistence.snapshot-store.local.dir = "target/akka-snapshots"
      |akka.persistence.journal.leveldb.native = off
      |
      |akka.actor.debug {
      |   receive = on
      |   autoreceive = on
      |   lifecycle = on
      |}
    """.stripMargin)

  val system = ActorSystem("catalog-es", config)
  val catalogActor = system.actorOf(CatalogActor.props("catalog1"), "catalogActor1")
  val catalogHistoryActor = system.actorOf(CatalogHistoryView.props("catalog1"), "catalogHistoryView1")

  val createCatalog = CreateCatalog("catalog1")
  val addAutrhor1 = AddAuthor("Mark", "Twain")
  val addAutrhor2 = AddAuthor("Jack", "London")
  val removeAutrhor1 = RemoveAuthor("Mark", "Twain")
  val removeAutrhor2 = RemoveAuthor("Jack", "London")

  catalogActor ! createCatalog

  catalogActor ! addAutrhor1

  catalogActor ! addAutrhor2

  catalogActor ! removeAutrhor1

  catalogActor ! removeAutrhor2

  implicit val timeout = Timeout(5 seconds)

  val authorsFuture = catalogActor ? GetAuthors

  val authors = Await.result(authorsFuture, timeout.duration)

  println(s"Authors: $authors")

  catalogHistoryActor ! Update(await = true)

  val catalogHistoryFuture = catalogHistoryActor ? GetCatalogHistory

  val catalogHistory = Await.result(catalogHistoryFuture, timeout.duration)

  println(s"Catalog History: $catalogHistory")

  system.terminate()
}
