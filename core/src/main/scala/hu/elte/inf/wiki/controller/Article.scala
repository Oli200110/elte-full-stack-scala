package hu.elte.inf.wiki.controller

import akka.http.scaladsl.model.MediaType
import hu.elte.inf.wiki.model
import hu.elte.inf.wiki.model.{Article, Image}
import hu.elte.inf.wiki.storage.Couchbase

class Article()(implicit couchbase: Couchbase) {
  protected val Articles = new Article.Storage()
  protected val Images = new Image.Storage()

  def update(articleID: String, body: String)(userID: String): model.Article = {
    val article = Articles.get(articleID).getOrElse(
      throw new NoSuchElementException(s"Article not found by ID [$articleID]!")
    )
    Articles.upsert(article.withNewChange(userID, body))
  }

  def get(articleID: String): Option[model.Article] = Articles.get(articleID)

  def create(body: String)(userID: String): model.Article = Articles.upsert(Article(userID, body))

  def getAll(): scala.collection.Seq[model.Article] = Articles.getAll

  def getImage(imageID: String): Option[Array[Byte]] = ???

  def uploadImage(bytes: Array[Byte], mediaType: MediaType): String = {
    require(!mediaType.isImage, "Media-type is not image!")
    require(!List("jpeg", "png").contains(mediaType.subType), "Only JPEG and PNG images are allowed!")
    mediaType.toString()
    Images.
  }
}
