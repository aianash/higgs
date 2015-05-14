package controllers.user

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal

import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

import akka.util.Timeout

import com.goshoplane.neutrino.feed._

import goshoplane.commons.core.protocols._, Implicits._

import actors.user._
import actions.auth._
import models.user._


/**
 * Controller for User apis
 *
 * @define needAuthentication
 * This requires user to be authenticated. Because the action requires
 * user id to complete the request
 */
object User extends Controller with UserJsonCombinators {

  // User client actor
  private val UserClient = Actors.userClient

  //////////////////// Controller Actions (mapped to Route) ///////////////////////////

  /**
   * Get authenticated user's details
   *
   * $needAuthentication
   */
  def me = (Authenticate andThen OnlyIfAuthenticated).async { implicit request =>
    implicit val timeout = Timeout(1 seconds)

    val infoF = UserClient ?= GetUserInfo(request.user.get.id)
    infoF.map(i => Ok(Json.toJson(i))).recover {
      case NonFatal(ex) =>
        InternalServerError(Json.obj("error" -> JsString("Couldn't fetch users info")))
    }
  }

}