package hu.elte.inf.wiki.controller

import hu.elte.inf.wiki.model
import hu.elte.inf.wiki.model.Article
import hu.elte.inf.wiki.storage.Couchbase

class Article()(implicit couchbase: Couchbase) {
  protected val Articles = new Article.Storage()

  def update(articleID: String, body: String): model.Article = {
    val article = Articles.get(articleID).getOrElse(
      throw new NoSuchElementException(s"Article not found by ID [$articleID]!")
    )
    Articles.upsert(article.withNewChange(???, body))
  }

  def get(articleID: String): Option[model.Article] = Articles.get(articleID)

  def create(body: String): model.Article = Articles.upsert(Article(???, body))

  def getAll(): scala.collection.Seq[model.Article] = Articles.getAll()

}
