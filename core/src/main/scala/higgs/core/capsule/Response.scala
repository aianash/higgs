package higgs.core.capsule

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.WebSocket.FrameFormatter

import neutrino.core.user._

sealed trait Response {
  def json: JsValue
}

case class Success(reqid: Int, result: JsValue) extends Response {
  import Response._
  def json = Json.toJson(this)
}

case class Message(userId: UserId, result: JsValue) extends Response {
  import Response._
  def json = Json.toJson(this)
}

case class Failure(error: String, code: Int, message: String) extends Response {
  import Response._
  def json = Json.toJson(this)
}

object Response {

  /**
   * Capsule response format
   */
  implicit val responseFormat: Format[Success] = (
    (__ \ "reqid").format[Int] and
    (__ \ "result").format[JsValue]
  ) ((reqid, result) =>
    Success(reqid, result),
    (response: Success) => (response.reqid, response.result)
  )

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
   * Error format
   */
  implicit val failureFormat: Format[Failure] = (
    (__ \ "error").format[String] and
    (__ \ "code").format[Int] and
    (__ \ "message").format[String]
  ) ((error, code, message) => Failure(error, code, message),
    (f: Failure) => (f.error, f.code, f.message)
  )

}