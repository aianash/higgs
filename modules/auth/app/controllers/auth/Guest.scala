package controllers.auth

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal

import javax.inject._

import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger

import akka.util.Timeout
import akka.actor.ActorRef

import goshoplane.commons.core.protocols._, Implicits._

import actors.auth._
import models.auth._

import neutrino.core.auth._

import higgs.core.result._

@Singleton
class Guest @Inject() (@Named("auth-service") authService: ActorRef)
  extends Controller with HttpResponseImplicits {

  val logger = Logger(this.getClass)

  def token = Action.async { implicit request =>
    implicit val timeout = Timeout(2 seconds)
    (authService ?= GetTokenForGuestUser).toHttpResponse
  }

}