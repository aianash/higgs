package higgs
package search

import scala.concurrent.duration._
import scala.util.Random

import play.api.libs.json._
import play.api.libs.functional.syntax._

import akka.actor.{Props, Actor, ActorLogging, Cancellable}
import akka.routing.FromConfig

import core.capsule._
import creed.client.search._, protocols._
import commons.catalogue._, attributes._, collection._
import neutrino.core.user.UserId


class SearchClient(capsule: SearchCapsule) extends Actor with ActorLogging {
  import SearchClient._

  import context.dispatcher

  val search = context.actorOf(FromConfig.props(), name = "search-service-router")

  def receive = {
    case req : GetSearchResultFor => log.info(s"Received GetSearchResultFor for ${req.searchId}")
      search ! req

    case req : UpdateQueryFor => log.info(s"Received UpdateQueryFor for ${req.searchId}")
      search ! req

    case res : SearchResult => log.info(s"Sending search result for ${res.searchId}")
      capsule.sendResponse(res, "/search/result")

    case msg @ QueryRecommendationsFor(searchId, reco) => log.info(s"Sending query recommendations for $searchId")
      capsule.sendMessage(
        Message(
          searchId.userId,
          "/query/suggestions/styles",
          Json.obj("sruid" -> searchId.sruid.toString) ++ Json.toJson(reco).asInstanceOf[JsObject]
        )
      )
  }

}

object SearchClient {
  import Search._

  def props(capsule: SearchCapsule) = Props(classOf[SearchClient], capsule)

  implicit val QueryRecommendationsWrites: Writes[QueryRecommendations] = (
    (__ \ "styles").write[Map[String, String]] ~
    (__ \ "filters").write[Map[String, QueryFilters]]
  ) { (recommend) =>
    import recommend._
    (
      styles.map(style => style.name -> filters(style).hashCode.toString).toMap,
      filters.values.map(f => f.hashCode.toString -> f).toMap
    )
  }

}