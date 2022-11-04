package hu.elte.inf.wiki.controller

import hu.elte.inf.wiki.model
import hu.elte.inf.wiki.storage.Couchbase

class User()(implicit couchbase: Couchbase) {
  protected val Users = new model.User.Storage()

  def update(userID: String, name: String): model.User = ???
  def get(userID: String): Option[model.User] = Users.get(userID)
  def login(mail: String, password: String): User.Session = ???
  def register(mail: String, password: String, name: String): User.Session = ???
}

object User {
  type Session = String
}
