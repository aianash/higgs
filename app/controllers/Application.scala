package controllers

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import play.api.mvc._
import play.api.Play.current

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

  def stream = WebSocket.tryAcceptWithActor[CapsuleRequest, CapsuleResponse] { request =>
    authenticate.verify(request) map { userIdO =>
      userIdO match {
        case Some(userId) => Right((a: ActorRef) => Props(clientConnectionFactory(userId, a)))
        case None         => Left(Forbidden)
      }
    }
  }

}