package higgs
package search

import play.api.libs.json._

import scalaz._, Scalaz._

import core.capsule._
import neutrino.core.user._

import akka.actor.ActorSystem


case class SearchId(userId: UserId, sruid: Long)

case class GetSearchResultFor(searchId: SearchId)
case class UpdateQueryFor(searchId: SearchId)

case class SearchResult(searchId: SearchId, result: List[JsObject])

case class StyleSuggestions(searchId: SearchId, styles: Seq[String])

object Search {

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
          UpdateQueryFor(SearchId(userId, sruid)).some
        case _ => none
      }
  }

  def processRequest(request: Any): Unit =
    client ! request

  def responseToJson(response: Any): Option[JsValue] = response match {
    case SearchResult(searchId, result) =>
      Json.obj(
        "sruid" -> searchId.sruid.toString,
        "result" -> result.foldLeft(Json.arr())(_ :+ _)
      ).some
    case _ => none
  }

}