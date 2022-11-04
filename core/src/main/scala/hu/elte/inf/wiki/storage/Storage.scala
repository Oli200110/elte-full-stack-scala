package hu.elte.inf.wiki.storage

import hu.elte.inf.wiki.model.Unique

import scala.reflect.ClassTag

abstract class Storage[T <: Unique[T] : ClassTag](collectionName: String)(
  implicit couchbase: Couchbase,
  converter: Converter[T]) {
  val collection = couchbase.scope.collection(collectionName)

  def getAll: scala.collection.Seq[T] =
    couchbase.cluster.query(
      s"SELECT * FROM `default`.`_default`.`$collectionName`"
    ).get.rowsAs[T].get

  def get(ID: String): Option[T] =
    collection
      .get(ID)
      .map(result => result.contentAs[T].toOption)
      .toOption
      .flatten

  def upsert(article: T): T = {
    collection
      .upsert(article.ID, article)
      .get
    article
  }

}
