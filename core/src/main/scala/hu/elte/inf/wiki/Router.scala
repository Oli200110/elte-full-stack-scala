package hu.elte.inf.wiki

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive1, Route}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import hu.elte.inf.wiki.Router.Protocol.Response.{modelArticleConverter, modelUserConverter}
import hu.elte.inf.wiki.storage.Couchbase
import org.json4s.{DefaultFormats, Formats, jackson}

class Router()(implicit couchbase: Couchbase) {
  implicit final protected val serializationFormat: Formats = DefaultFormats.withBigDecimal
  implicit final protected val serializationDriver = jackson.Serialization

  protected val userController = new controller.User()
  protected val articleController = new controller.Article()

  val route: Route =
    pathPrefix("api") {
      pathPrefix("v1") {
        path("ping") {
          get {
            complete(StatusCodes.NoContent)
          }
        } ~
          path("login") {
            post {
              entity(as[Router.Protocol.Request.Login]) {
                login =>
                  complete {
                    userController.login(login.mail, login.password)
                  }
              }
            }
          } ~
          path("register") {
            post {
              entity(as[Router.Protocol.Request.Register]) {
                register =>
                  complete {
                    userController.register(register.mail, register.password, register.name)
                  }
              }
            }
          } ~
          path("user") {
            withSession {
              sessionID =>
                get {
                  val userID: String = ???
                  complete {
                    userController.get(userID).map(_.toProtocol)
                  }
                } ~
                  patch {
                    entity(as[Router.Protocol.Request.User]) {
                      user =>
                        val userID: String = ???
                        complete {
                          userController.update(userID, user.name).toProtocol
                        }
                    }
                  }
            }
          } ~
          pathPrefix("articles") {
            pathEnd {
              get {
                complete {
                  articleController.getAll().map(_.toProtocol)
                }
              } ~
                post {
                  entity(as[Router.Protocol.Request.Article]) {
                    article =>
                      complete {
                        articleController.create(article.body).toProtocol
                      }
                  }
                }
            } ~
              path(Segment) {
                articleID =>
                  get {
                    complete {
                      articleController.get(articleID).map(_.toProtocol)
                    }
                  } ~
                    patch {
                      entity(as[Router.Protocol.Request.Article]) {
                        article =>
                          complete {
                            articleController.update(articleID, article.body).toProtocol
                          }
                      }
                    }
              }
          }
      }
    }

  def withSession: Directive1[Option[String]] =
    optionalHeaderValueByName("Session").flatMap {
      case Some(sessionID) => provide(sessionID)
      case None            => reject
    }

}

object Router {

  object Protocol {

    object Request {
      case class Login(mail: String, password: String)
      case class Register(mail: String, password: String, name: String)
      case class Article(body: String)
      case class User(name: String)
    }

    object Response {

      implicit class modelArticleConverter(article: model.Article) {
        def toProtocol: Article = Article(article)
      }

      case class Article(body: String)

      object Article {
        def apply(article: model.Article): Article = Article(article.body)
      }

      implicit class modelUserConverter(user: model.User) {
        def toProtocol: User = User(user)
      }

      case class User(userID: String, name: String)

      object User {
        def apply(user: model.User): User = User(user.ID, user.name)
      }

    }

  }

}
