package higgs.core.capsule

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

case class Request(reqid: Int, reqType: RequestType, userId: UserId, uri: String, params: JsValue)

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
    (__ \ "reqid").format[String] and
    (__ \ "type").format[String] and
    (__ \ "userid").format[String] and
    (__ \ "uri").format[String] and
    (__ \ "params").format[JsValue]
  ) ((reqid, reqType, userId, uri, params) =>
    Request(reqid.toInt, RequestType(reqType), UserId(userId.toLong), uri, params),
    (request: Request) =>
      (request.reqid.toString, request.reqType.value, request.userId.uuid.toString, request.uri, request.params)
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

