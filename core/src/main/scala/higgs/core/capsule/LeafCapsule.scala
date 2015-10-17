package higgs.core.capsule

import scala.concurrent.Promise
import scala.util.control.NonFatal

import scalaz.{Success => _, _}, Scalaz._

import play.api.libs.json._
import play.api.Logger

import higgs.core.capsule.channel._


abstract class LeafCapsule[T <: LeafCapsule[T]](implicit hashifier: Hashifier[T]) extends Capsule {

  private val logger = Logger(getClass)

  def parseRequest(request: Request): Option[Any]
  def processRequest(request: Any): Unit
  def responseToJson(response: Any): Option[JsValue]

  private[capsule] val channel = (new ManyToOneChannel(hashifier, responseToJson)).some

  val handleRequest = (request: Request) =>
    parseRequest(request).map { parsedReq =>
      request.reqType match {
        case RequestType.GET =>
          val p = channel.map(_.registerRequest(parsedReq, request.reqid.get)).get
          processRequest(parsedReq)
          Right(p)

        case RequestType.POST =>
          processRequest(parsedReq)
          Right(Promise.successful(Success(request.reqid.getOrElse(-1), "", JsNull).asInstanceOf[Response]))
      }
    } getOrElse Left(request)

  def sendResponse(response: Any, responseType: String): Unit =
    try channel.map(_.sendResponse(response, responseType))
    catch {
      case NonFatal(ex) =>
        logger.error("Error occured while sending response", ex)
    }

  def sendMessage(msg: Message): Unit =
    try channel.map(_.sendMessage(msg))
    catch {
      case NonFatal(ex) =>
        logger.error("Error occured while sending message", ex)
    }

}