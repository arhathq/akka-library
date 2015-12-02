package library.core.persistence

/**
  * @author Alexander Kuleshov
  */
object CatalogProtocol {

  sealed trait CatalogEvt

  case class CatalogCreated(id: String) extends CatalogEvt
  case class AuthorAdded(author: Author) extends CatalogEvt
  case class AuthorUpdated(author: Author) extends CatalogEvt
  case class AuthorRemoved(id: Int) extends CatalogEvt

  case class Author(id: Option[Int] = None, firstname: String, lastname: String)
}