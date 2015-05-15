package controllers.search

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal

import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.Logger

import akka.util.Timeout

import com.goshoplane.creed.search._

import goshoplane.commons.core.protocols._, Implicits._

import actors.search._
import actions.auth._
import models.search._


/**
 * Controller for Search related REST apis
 *
 * @define needAuthentication
 * This requires user to be authenticated. Because the action requires
 * user id to complete the request
 */
object Search extends Controller with SearchJsonCombinators {

  val log = Logger(this.getClass)

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
      val param = QueryParam(value = Some("levis men's jeans"))
      val searchQuery = CatalogueSearchQuery(queryText = searchRequest.queryText, params = Map("brand" -> param))
      val catalogueSearchRequest = CatalogueSearchRequest(
        searchId  = searchId,
        query     = searchQuery,
        pageIndex = searchRequest.pageIndex,
        pageSize  = searchRequest.pageSize
      )

      implicit val timeout = Timeout(100 seconds)

      val resultF = SearchClient ?= SearchCatalogue(catalogueSearchRequest)
      resultF.map(r => Ok(Json.toJson(r))).recover {
        case NonFatal(ex) =>
          log.error(s"Caught error [${ex.getMessage}] while getting search result", ex)
          InternalServerError(Json.obj("error" -> JsString("Couldn't fetch search result for search id = ${searchId.userId.uuid}.${searchId.sruid}")))
      }
    }

}