package controllers

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

import actors._
import actions._


/**
 * Controller for Feed (both common and user specific feeds)
 *
 * @define feedFilter
 * FeedFilter is parsed out of the request body. Passing FeedFilter
 * is mandatory, even if its empty filter
 *
 * @define needAuthentication
 * This requires user to be authenticated. Because the action requires
 * user id to complete the request
 *
 */
object Feed extends Controller with models.FeedJsonCombinators {

  // Feed client Actor
  private val FeedClient = Actors.feedClient

  //////////////////// Controller Actions (mapped to Route) ///////////////////////////

  /**
   * Get common feed
   *
   * $feedFilter
   */
  def common = Action.async(parse.json[FeedFilter]) { implicit request =>
    val filter = request.body

    implicit val timeout = Timeout(1 seconds)

    val feedF = FeedClient ?= GetCommonFeed(filter)
    feedF.map(f => Ok(Json.toJson(f))).recover {
      case NonFatal(ex) =>
        InternalServerError(Json.obj("error" -> JsString("Couldn't fetch common feeds")))
    }
  }

  /**
   * Get User feed
   *
   * $needAuthentication
   *
   * $feedFilter
   */
  def user = (Authenticate andThen OnlyIfAuthenticated).async(parse.json[FeedFilter]) { implicit request =>
    val filter = request.body
    val userId = request.user.get.id

    implicit val timeout = Timeout(1 seconds)

    val feedF = FeedClient ?= GetUserFeed(userId, filter)
    feedF.map(f => Ok(Json.toJson(f))).recover {
      case NonFatal(ex) =>
        InternalServerError(Json.obj("error" -> JsString(s"Couldn't fetch feeds for user $userId")))
    }
  }

}