package higgs.core.capsule

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.WebSocket.FrameFormatter

import neutrino.core.user._

sealed trait Response {
  def json: JsValue
}

case class Success(reqid: Int, responseType: String, result: JsValue) extends Response {
  import Response._
  def json = Json.toJson(this)
}

case class Message(userId: UserId, messageType: String, result: JsValue) extends Response {
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
  implicit val responseFormat: Writes[Success] = (
    (__ \ "reqid").write[String] and
    (__ \ "responseType").write[String] and
    (__ \ "data").write[JsValue]
  ) ((response: Success) => (response.reqid.toString, response.responseType, response.result))

  /**
   * Message format
   */
  implicit val messageFormat: Writes[Message] = (
    (__ \ "uuid").write[String] and
    (__ \ "messageType").write[String] and
    (__ \ "data").write[JsValue]
  ) ((message: Message) => (message.userId.uuid.toString, message.messageType, message.result))

  /**
   * Error format
   */
  implicit val failureFormat: Writes[Failure] = (
    (__ \ "error").write[String] and
    (__ \ "code").write[Int] and
    (__ \ "message").write[String]
  ) ((f: Failure) => (f.error, f.code, f.message))

}