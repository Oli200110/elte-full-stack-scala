package hu.elte.inf.wiki

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class Controller() {

  val route: Route =
    pathPrefix("api") {
      pathPrefix("v1") {
        path("ping") {
          get {
            complete(StatusCodes.NoContent)
          }
        } ~
          pathPrefix("articles") {
            get {
              complete(StatusCodes.OK)
            }
          }
      }
    }

}
