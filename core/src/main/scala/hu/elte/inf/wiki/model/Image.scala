package hu.elte.inf.wiki.model

import hu.elte.inf.wiki.storage.{Converter, Couchbase}

case class Image(ID: String, bytes: Array[Byte]) extends Unique[Image] {

}

object Image {

  implicit object Converter extends Converter[Image]

  class Storage(implicit couchbase: Couchbase) {
    val collection = couchbase.scope.collection("images")
  }
}