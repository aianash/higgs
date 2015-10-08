package higgs.core.capsule

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.WebSocket.FrameFormatter

import neutrino.core.user._

case class Response(reqid: Int, result: JsValue)
case class Message(userId: UserId, result: JsValue)

object Response {

  /**
   * Capsule response format
   */
  implicit val responseFormat: Format[Response] = (
    (__ \ "reqid").format[Int] and
    (__ \ "result").format[JsValue]
  ) ((reqid, result) =>
    Response(reqid, result),
    (response: Response) => (response.reqid, response.result)
  )

  /**
   * Formats WebSocket frames to be Responses.
   */
  implicit def responseFrameFormatter: FrameFormatter[Response] = FrameFormatter.jsonFrame.transform(
    Response => Json.toJson(Response),
    json => Json.fromJson[Response](json).fold(
      invalid => throw new RuntimeException("Bad capsule response on WebSocket: " + invalid),
      valid => valid
    )
  )

}

object Message {

  /**
   * Message format
   */
  implicit val messageFormat: Format[Message] = (
    (__ \ "userid").format[Long] and
    (__ \ "result").format[JsValue]
  ) ((userId, result) =>
    Message(UserId(userId), result),
    (message: Message) => (message.userId.uuid, message.result)
  )

  /**
   * Formats WebSocket frames to be Responses.
   */
  implicit def messageFrameFormatter: FrameFormatter[Message] = FrameFormatter.jsonFrame.transform(
    Message => Json.toJson(Message),
    json => Json.fromJson[Message](json).fold(
      invalid => throw new RuntimeException("Bad capsule message on WebSocket: " + invalid),
      valid => valid
    )
  )

}