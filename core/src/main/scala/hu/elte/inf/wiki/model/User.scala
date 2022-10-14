package hu.elte.inf.wiki.model

case class User(
  ID: String,
  name: String,
  mail: String,
  password: String,
  created: Long) {}
