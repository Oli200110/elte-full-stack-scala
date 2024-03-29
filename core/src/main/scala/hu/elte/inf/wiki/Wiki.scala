package hu.elte.inf.wiki

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import hu.elte.inf.wiki.storage.Couchbase

class Wiki() extends Logger {

  def start(): Unit = {
    log.info("Starting HTTP server.")

    implicit val system: ActorSystem = ActorSystem()
    implicit val couchbase: Couchbase = Couchbase()
    val router = new Router()
    Http().newServerAt("localhost", 8080).bind(router.route)
  }

}

object Wiki {
  def main(arguments: Array[String]) = new Wiki().start()
}
