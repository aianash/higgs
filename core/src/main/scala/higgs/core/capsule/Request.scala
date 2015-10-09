package higgs.core.capsule

import scalaz._, Scalaz._

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.WebSocket.FrameFormatter

import neutrino.core.user._

sealed trait RequestType {
  def value: String
}

object RequestType {

  def apply(reqType: String) = reqType match {
    case "get"  => GET
    case "post" => POST
  }

  object GET extends RequestType {
    val value = "get"
  }

  object POST extends RequestType {
    val value = "post"
  }

}

case class Request(reqid: Option[Int], timestamp: Option[Long], reqType: RequestType, uri: String, params: JsValue)

object Request {

  /**
   * Client event format
   * - reqid  : Request Id
   * - type   : which module the request belongs to
   * - UserId : User Id
   * - uri    : uri is needed
   * - params : params required to complete the request
   */
  implicit def requestFormat: Format[Request] = (
    (__ \ "reqid")      .formatNullable[String] and
    (__ \ "timestamp")  .formatNullable[String] and
    (__ \ "type")       .format[String] and
    (__ \ "uri")        .format[String] and
    (__ \ "params")     .format[JsValue]
  ) ((reqid, timestamp, reqType, uri, params) =>
    Request(reqid.map(_.toInt), timestamp.map(_.toLong), RequestType(reqType), uri, params),
    (request: Request) =>
      (request.reqid.toString.some, request.timestamp.toString.some, request.reqType.value, request.uri, request.params)
  )

  /**
   * Formats WebSocket frames to be Requests.
   */
  implicit def requestFormatter: FrameFormatter[Request] = FrameFormatter.jsonFrame.transform(
    request => Json.toJson(request),
    json => Json.fromJson[Request](json).fold(
      invalid => throw new RuntimeException("Bad client event on WebSocket: " + invalid),
      valid => valid
    )
  )

}

