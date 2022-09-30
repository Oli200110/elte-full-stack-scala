package hu.elte.inf.wiki.model

case class User(
  ID: String,
  name: String,
  password: String,
  created: Long) {}
