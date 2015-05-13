package controllers

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal

import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._

import akka.util.Timeout

import com.goshoplane.creed.search._

import goshoplane.commons.core.protocols._, Implicits._

import actors._
import actions._
import models.SearchRequest


/**
 * Controller for Search related REST apis
 *
 * @define needAuthentication
 * This requires user to be authenticated. Because the action requires
 * user id to complete the request
 */
object Search extends Controller with models.SearchJsonCombinators {

  // Search client actor
  private val SearchClient = Actors.searchClient


  //////////////////// Controller Actions (mapped to Route) ///////////////////////////

  /**
   * Search catalogue items and return result grouped by stores
   *
   * {{{
   * POST /v1/search/:sruid
   * BODY =
   * {
   *   "queryText": "search query here",
   *   "pageIndex": 1,
   *   "pageSize": 10
   * }
   * }}}
   *
   * $needAuthentication
   * Search for non authenticated user to be done soon
   */
  def search(sruid: Long) =
    (Authenticate andThen OnlyIfAuthenticated).async(parse.json[SearchRequest]) { implicit request =>
      val searchRequest = request.body
      val searchId = CatalogueSearchId(userId = request.user.get.id, sruid = sruid)
      val searchQuery = CatalogueSearchQuery(queryText = searchRequest.queryText)
      val catalogueSearchRequest = CatalogueSearchRequest(
        searchId  = searchId,
        query     = searchQuery,
        pageIndex = searchRequest.pageIndex,
        pageSize  = searchRequest.pageSize
      )

      implicit val timeout = Timeout(5 seconds)

      val resultF = SearchClient ?= SearchCatalogue(catalogueSearchRequest)
      resultF.map(r => Ok(Json.toJson(r))).recover {
        case NonFatal(ex) =>
          InternalServerError(Json.obj("error" -> JsString("Couldn't fetch search result for search id = ${searchId.userId.uuid}.${searchId.sruid}")))
      }
    }

}