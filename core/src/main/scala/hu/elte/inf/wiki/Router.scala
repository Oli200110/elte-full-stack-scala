package hu.elte.inf.wiki

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.StreamLimitReachedException
import akka.util.ByteString
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import hu.elte.inf.wiki.Router.Protocol.Response.modelArticleConverter
import hu.elte.inf.wiki.storage.Couchbase

import scala.util.{Failure, Success}

class Router()(implicit protected val couchbase: Couchbase)
 extends routers.Base with routers.User with Logger {

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
          userRoutes ~
          pathPrefix("articles") {
            pathEnd {
              get {
                complete {
                  articleController.getAll().map(_.toProtocol)
                }
              } ~
                withSession {
                  session =>
                    post {
                      entity(as[Router.Protocol.Request.Article]) {
                        article =>
                          complete {
                            articleController.create(article.body)(session.userID).toProtocol
                          }
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
                    withSession {
                      session =>
                        patch {
                          entity(as[Router.Protocol.Request.Article]) {
                            article =>
                              complete {
                                articleController.update(articleID, article.body)(
                                  session.userID
                                ).toProtocol
                              }
                          }
                        }
                    } ~
                    pathPrefix("images") {
                      pathEnd {
                        withSession {
                          session =>
                            extractRequestContext {
                              context =>
                                withSizeLimit(4 * 1024 * 1024) {
                                  fileUpload("image") {
                                    case (metadata, byteSource) =>
                                      onComplete(byteSource.runFold(ByteString.empty)(_ ++ _)(
                                        context.materializer
                                      )) {
                                        case Failure(_: StreamLimitReachedException) =>
                                          complete(StatusCodes.PayloadTooLarge)
                                        case Failure(exception) =>
                                          log.error(
                                            s"Upload failed due to exception [${exception.getClass.getCanonicalName}]!"
                                          )
                                          exception.printStackTrace()
                                          complete(StatusCodes.InternalServerError)
                                        case Success(uploadedBytes) =>
                                          complete(
                                            articleController.uploadImage(
                                              uploadedBytes.toArray[Byte],
                                              metadata.contentType.mediaType
                                            )
                                          )
                                      }
                                  }
                                }
                            }
                        }
                      } ~
                        path(Segment) {
                          imageID =>
                            get {
                              complete(
                                articleController.getImage(imageID)
                              )
                            }
                        }
                    }
              }
          }
      }
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

      case class Article(body: String, images: Seq[String])

      object Article {
        def apply(article: model.Article): Article = Article(article.body, article.images)
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
