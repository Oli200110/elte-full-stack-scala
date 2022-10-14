package hu.elte.inf.wiki.controller

import hu.elte.inf.wiki.model

class Article() {
  def update(articleID: String, body: String): model.Article = ???
  def get(articleID: String): Option[model.Article] = ???
  def create(body: String): model.Article = ???
  def getAll(): Seq[model.Article] = ???

}
