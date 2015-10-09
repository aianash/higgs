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

/**
 * Controller for Auth (ie receiving access token) for
 * app authenticated used (thru Facebook)
 */
@Singleton
class Login @Inject() (@Named("auth-service") authService: ActorRef)
  extends Controller with AuthJsonCombinators with HttpResponseImplicits {

  val log = Logger(this.getClass)

  // service to verifying token
  // private val AuthService = Actors.authService

  //////////////////// Controller Actions (mapped to Route) ///////////////////////////

  /**
   * Get access token for verified fb auth info
   */
  def withFacebook = Action.async(parse.json[FBAuthInfo]) { implicit request =>
    val authInfo = request.body
    implicit val timeout = Timeout(2 seconds)

    (authService ?= VerifyAndGetTokenFor(authInfo)).toHttpResponse
  }

  /**
   * Get access token for verified google auth info
   */
  def withGoogle = Action.async(parse.json[GoogleAuthInfo]) { implicit request =>
    val authInfo = request.body
    implicit val timeout = Timeout(2 seconds)

    (authService ?= VerifyAndGetTokenFor(authInfo)).toHttpResponse
  }

}