package library.core.persistence

/**
  * @author Alexander Kuleshov
  */
object CatalogProtocol {

  sealed trait CatalogEvt

  case class CatalogCreated(id: String) extends CatalogEvt
  case class AuthorAdded(firstname: String, lastname: String) extends CatalogEvt
  case class AuthorUpdated(firstname: String, lastname: String) extends CatalogEvt
  case class AuthorRemoved(firstname: String, lastname: String) extends CatalogEvt

}