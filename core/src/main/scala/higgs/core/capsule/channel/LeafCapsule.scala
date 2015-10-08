package higgs.core.capsule

import scala.concurrent.Promise

import scalaz._, Scalaz._

import play.api.libs.json._

import higgs.core.capsule.channel._


abstract class LeafCapsule[T <: LeafCapsule[T]](implicit hashifier: Hashifier[T]) extends Capsule {

  def parseRequest(request: Request): Option[Any]
  def processRequest(request: Any): Unit
  def responseToJson(response: Any): Option[JsValue]

  val channel = (new ManyToOneChannel(hashifier, responseToJson)).some

  val handleRequest = (request: Request) =>
    parseRequest(request).map { parsedReq =>
      request.reqType match {
        case RequestType.GET =>
          val p = channel.map(_.registerRequest(parsedReq, request.reqid)).get
          processRequest(parsedReq)
          Right(p)

        case RequestType.POST =>
          processRequest(parsedReq)
          Right(Promise.successful(Response(request.reqid, JsNull)))
      }
    } getOrElse Left(request)

  def sendResponse(response: Any): Unit = channel.map(_.sendResponse(response))

}