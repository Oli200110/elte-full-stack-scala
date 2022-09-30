package hu.elte.inf.wiki.model

import hu.elte.inf.wiki.model.Article.Change

case class Article(
  ID: String,
  body: String,
  images: Seq[String],
  changes: Seq[Change]) {
  require(changes.nonEmpty, "At least one change must exist within `changes`!")

  def withNewChange(userID: String, body: String): Article = ???
}

object Article {

  case class Change(timestamp: Long, userID: String) {
    require(timestamp > 0, "Timestamp must be greater then zero!")
  }

}
