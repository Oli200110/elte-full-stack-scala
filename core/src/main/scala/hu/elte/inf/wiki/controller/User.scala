package hu.elte.inf.wiki.controller

import hu.elte.inf.wiki.model

class User() {
  def update(userID: String, name: String): model.User = ???
  def get(userID: String): Option[model.User] = ???
  def login(mail: String, password: String): User.Session = ???
  def register(mail: String, password: String, name: String): User.Session = ???
}

object User {
  type Session = String
}
