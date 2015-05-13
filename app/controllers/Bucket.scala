package controllers

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal

import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._

import akka.util.Timeout

import com.goshoplane.neutrino.service._
import com.goshoplane.neutrino.shopplan._

import goshoplane.commons.core.protocols._, Implicits._

import actors._
import actions._


/**
 * Controller for Bucket apis
 *
 * @define needAuthentication
 * This requires user to be authenticated. Because the action requires
 * user id to complete the request
 */
object Bucket extends Controller with models.BucketJsonCombinators {

  // Bucket client actor
  private val BucketClient = Actors.bucketClient

  //////////////////// Controller Actions (mapped to Route) ///////////////////////////

  /**
   * Get bucket store with specified fields as comma separated fields e.g.
   * {{{
   * GET /v1/bucket/stores?fields=Name,Address,ItemTypes
   * }}}
   * $needAuthentication
   */
  def stores(fieldsString: String) = (Authenticate andThen OnlyIfAuthenticated).async { implicit request =>
    val fields = fieldsString.split(",").flatMap(BucketStoreField.valueOf(_))

    implicit val timeout = Timeout(1 seconds)
    val storesF = BucketClient ?= GetBucketStores(request.user.get.id, fields)
    storesF.map(s => Ok(Json.toJson(s))).recover {
      case NonFatal(ex) =>
        InternalServerError(Json.obj("error" -> JsString("Couldn't fetch bucket stores")))
    }
  }


  /**
   * Perform Create/Update/Delete operation on bucket items
   * $needAuthentication
   * [NOTE] currently only support add item to bucket
   *
   * {{{
   * POST /v1/bucket/cud
   * BODY (json) =
   * {
   *   "adds" : [
   *     {
   *       "storeId": {"stuid": 3900299992},
   *       "ctuid": 32000000292
   *     }
   *   ]
   * }
   * }}}
   */
  def cud = (Authenticate andThen OnlyIfAuthenticated).async(parse.json[CUDBucket]) { implicit request =>
    val cud = request.body

    implicit val timeout = Timeout(1 seconds)
    val successF = BucketClient ?= ModifyBucket(request.user.get.id, cud)
    successF.map(s => Ok(Json.obj("success" -> JsBoolean(s)))).recover {
      case NonFatal(ex) =>
        InternalServerError(Json.obj("error" -> JsString("Couldn't fetch bucket stores")))
    }
  }

}