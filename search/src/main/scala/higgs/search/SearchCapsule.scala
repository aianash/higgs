package higgs
package search

import play.api.libs.json._
import play.api.libs.functional.syntax._

import scalaz._, Scalaz._

import core.capsule._
import neutrino.core.user._
import creed.core.search._, protocols._
import commons.catalogue.attributes._

import akka.actor.ActorSystem


object Search {

  implicit val queryFilterReads: Format[QueryFilters] = (
    (__ \ "colors").formatNullable[Seq[String]] ~
    (__ \ "sizes").formatNullable[Seq[String]]
  ) ({ (colorsO, sizesO) =>
      QueryFilters(List(
        colorsO.map(ColorFilter(_)),
        sizesO.map(SizesFilter(_))
      ).flatten)
    },
    { (qf: QueryFilters) =>
      import qf._
      (filter[ColorFilter].map(_.colors),
       filter[SizesFilter].map(_.sizes))
    })

  implicit val queryRead: Reads[Query] = (
    (__ \ "queryStr").read[String] ~
    (__ \ "styles").readNullable[Map[String, String]] ~
    (__ \ "filters").readNullable[Map[String, QueryFilters]]
  ) { (queryStr, stylesO, filtersO) =>
    val filters = filtersO getOrElse Map.empty[String, QueryFilters]
    val styles = stylesO getOrElse Map.empty[String, String]
    Query(
      queryStr = queryStr,
      filters =
        styles.map { case (style, filterId) =>
          ClothingStyle(style) -> filters(filterId)
        }
      )
  }

  implicit val SearchHashifier = new Hashifier[SearchCapsule] {
    implicit val userIdH = Hash.by[UserId, Long](_.uuid)
    implicit val userIdsruid = Hash.tuple1[UserId, Long]
    implicit val searchIdH = Hash.by[SearchId, (UserId, Long)](sr => sr.userId -> sr.sruid)

    val GetSearchResultForH = Hash.by[GetSearchResultFor, SearchId](_.searchId)
    val SearchResultForH = Hash.by[SearchResult, SearchId](_.searchId)

    def hashAny = GetSearchResultForH :: SearchResultForH
  }

}

import Search._

class SearchCapsule(system: ActorSystem) extends LeafCapsule[SearchCapsule] {

  val client = system.actorOf(SearchClient.props(this))

  def parseRequest(request: Request) = request match {
    case Request(_, _, userId, RequestType.GET, uri, params : JsObject) =>
      uri match {
        case "/search/result" =>
          val sruid = (params \ "sruid").as[String].toLong
          GetSearchResultFor(SearchId(userId, sruid)).some
        case _ => none
      }
    case Request(_, _, userId, RequestType.POST, uri, params) =>
      uri match {
        case "/query/update" =>
          val sruid = (params \ "sruid").as[String].toLong
          UpdateQueryFor(
            searchId = SearchId(userId, sruid),
            query    = (params \ "query").as[Query]).some
        case _ => none
      }
  }

  def processRequest(request: Any): Unit = client ! request

  def responseToJson(response: Any): Option[JsValue] = response match {
    case SearchResult(searchId, result, _) =>
      Json.obj(
        "sruid" -> searchId.sruid.toString
        // ,
        // "result" -> result.foldLeft(Json.arr())(_ :+ _.json)
      ) some
    case _ => none
  }

}