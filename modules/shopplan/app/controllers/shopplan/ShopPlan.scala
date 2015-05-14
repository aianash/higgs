package controllers.shopplan

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

import actors.shopplan._
import actions.auth._
import models.shopplan._


/**
 * Controllers for ShopPlan apis
 *
 * @define needAuthentication
 * This requires user to be authenticated. Because the action requires
 * user id to complete the request
 */
object ShopPlan extends Controller with ShopPlanJsonCombinators {

  // ShopPlan client actor
  private val ShopPlanClient = Actors.shopPlanClient

  //////////////////// Controller Actions (mapped to Route) ///////////////////////////

  /**
   * Get list of user's own shop plans (ie. created by user)
   * {{{
   * GET /v1/shopplan/list/own?fields=Title,Stores,CatalogueItems
   * }}}
   * $needAuthentication
   */
  def listOwn(fieldsString: String) = (Authenticate andThen OnlyIfAuthenticated).async { implicit request =>
    val fields = toShopPlanFields(fieldsString)

    implicit val timeout = Timeout(1 seconds)
    val shopplansF = ShopPlanClient ?= GetOwnShopPlans(request.user.get.id, fields)
    shopplansF.map(s => Ok(Json.toJson(s))).recover {
      case NonFatal(ex) =>
        InternalServerError(Json.obj("error" -> JsString("Couldn't fetch user's own shopplans")))
    }
  }


  /**
   * Get ShopPlan's details as specified in fields
   * {{{
   * GET /v1/shopplan/:suid?fields=Title,Stores,CatalogueItems
   * }}}
   * $needAuthentication
   */
  def get(suid: Long, fieldsString: String) = (Authenticate andThen OnlyIfAuthenticated).async { implicit request =>
    val fields = toShopPlanFields(fieldsString)
    val shopplanId = ShopPlanId(createdBy = request.user.get.id, suid = suid)

    implicit val timeout = Timeout(1 seconds)
    val shopplanF = ShopPlanClient ?= GetShopPlan(shopplanId, fields)
    shopplanF.map(s => Ok(Json.toJson(s))).recover {
      case NonFatal(ex) =>
        InternalServerError(Json.obj("error" -> JsString("Couldn't fetch shopplan")))
    }
  }


  /**
   * Create a new shop plan with the given data in request.body as json
   * {{{
   * GET /v1/shopplan/create
   * BODY =
   * {
   *   "meta": {
   *     "title": "Title of shopplan"
   *   },
   *   "destinations": {
   *     "adds": [
   *       {
   *         "destId": {
   *           "shopplanId": {
   *             "createdBy": {"uuid": -1},
   *             "suid": -1
   *           },
   *           "dtuid": 939900292994
   *         },
   *         "address": {
   *           "gpsLoc": {
   *             "lat": 12.0010,
   *             "lng": 20.334
   *           }
   *         }
   *       }
   *     ]
   *   },
   *   "invites": {
   *     "adds": [
   *       {
   *         "uuid": 2990289902
   *       }
   *     ]
   *   },
   *   "items": {
   *     "adds": [
   *       {
   *         "storeId": { "stuid": 29902000202 },
   *         "ctuid": 2900299992
   *       }
   *     ]
   *   }
   * }
   *
   * }}}
   *
   * $needAuthentication
   */
  def create = (Authenticate andThen OnlyIfAuthenticated).async(parse.json[CUDShopPlan]) { implicit request =>
    val cud = request.body

    implicit val timeout = Timeout(5 seconds)
    val shopplanIdF = ShopPlanClient ?= CreateShopPlan(request.user.get.id, cud)
    shopplanIdF.map(s => Ok(Json.toJson(s))).recover {
      case NonFatal(ex) =>
        InternalServerError(Json.obj("error" -> JsString("Couldn't create shopplan")))
    }
  }


  /**
   * End a shop plan
   * {{{
   * DELETE /v1/shopplan/:suid
   * }}}
   * $needAuthentication
   */
  def end(suid: Long) = (Authenticate andThen OnlyIfAuthenticated).async { implicit request =>
    val shopplanId = ShopPlanId(createdBy = request.user.get.id, suid = suid)

    implicit val timeout = Timeout(1 seconds)
    val successF = ShopPlanClient ?= EndShopPlan(shopplanId)
    successF.map(s => Ok(Json.obj("success" -> JsBoolean(s)))).recover {
      case NonFatal(ex) =>
        InternalServerError(Json.obj("error" -> JsString("Couldn't end shopplan")))
    }
  }


  /**
   * Convert Request query paramter's comma separated fields to
   * Array of ShopPlanField
   * @type {[type]}
   */
  private def toShopPlanFields(str: String) =
    str.split(",").flatMap(ShopPlanField.valueOf(_))

}