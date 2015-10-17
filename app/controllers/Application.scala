package controllers

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal

import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.Logger

import javax.inject._

import higgs.core.capsule._
import higgs.core.capsule.{Request => CapsuleRequest, Response => CapsuleResponse}
import higgs.core.capsule.Request._
import higgs.core.capsule.Response._

import actors._
import actions.auth._

@Singleton
class Application @Inject() (
    clientConnectionFactory: ClientConnection.Factory,
    authenticate: Authenticate
  ) extends Controller {

  val logger = Logger(getClass)

  def stream = WebSocket.tryAcceptWithActor[CapsuleRequest, JsValue] { request =>
    logger.debug(s"Received request $request")
    authenticate.verify(request) map { userIdO =>
      userIdO match {
        case Some(userId) =>
          try Right((a: ActorRef) => Props(clientConnectionFactory(userId, a)))
          catch {
            case NonFatal(ex) =>
              logger.error(s"Error while creating client connection actor for request $request", ex)
              Left(InternalServerError)
          }
        case None =>
          logger.warn(s"Unauthorized request received $request")
          Left(Forbidden)
      }
    }
  }

}
