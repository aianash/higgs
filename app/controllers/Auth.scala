package controllers

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal

import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger

import akka.util.Timeout

import goshoplane.commons.core.protocols._, Implicits._

import actors._
import models._


/**
 * Controller for Auth (ie receiving access token) for
 * app authenticated used (thru Facebook)
 */
object Auth extends Controller with AuthJsonCombinators {

  val log = Logger(this.getClass)

  // service to verifying token
  private val AuthService = Actors.authService

  //////////////////// Controller Actions (mapped to Route) ///////////////////////////

  /**
   * Get higgs access token for a verified fb auth info
   * in body as json
   */
  def token = Action.async(parse.json[FBAuthInfo]) { implicit request =>
    val fbInfo = request.body

    implicit val timeout = Timeout(1 seconds) // [TO DO] Configure timeouts

    val tokenF = AuthService ?= VerifyAndGetTokenFor(fbInfo)
    tokenF.map(token => Ok(Json.obj("token" -> JsString(token))))
      .recover {
        case InvalidCredentialsException(msg) =>
          BadRequest(Json.obj("error" -> JsString(msg)))

        case NonFatal(ex) =>
          log.error("Caught error [${ex.getMessage}] while creating token", ex)
          InternalServerError(Json.obj("error" -> JsString("Some internal error while creating token")))
      }
  }

}