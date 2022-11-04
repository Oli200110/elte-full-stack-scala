package hu.elte.inf.wiki.model

import hu.elte.inf.wiki.storage
import hu.elte.inf.wiki.storage.{Converter, Couchbase}

case class User(
  ID: String,
  name: String,
  mail: String,
  password: String,
  created: Long)
 extends Unique[User] {}

object User {

  implicit object Converter extends Converter[User]

  class Storage(implicit couchbase: Couchbase) extends storage.Storage[User]("users")

}
