package higgs.core.capsule

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal
import scala.util.{Success => TSuccess, Failure => TFailure}

import akka.actor.ActorRef

import play.api.libs.json._
import play.api.http.Status._
import play.api.Logger


class Process(capsule: Capsule, upstream: ActorRef) {

  private val logger = Logger(getClass)
  private val maxActiveReq = 4
  private val ring = new Ring(maxActiveReq)

  def process(request: Request)(implicit ec: ExecutionContext): Unit = {
    import request._
    import RequestType._

    reqType match {
      case POST => capsule.handleRequest(request)

      case GET if reqid.nonEmpty && timestamp.nonEmpty =>
        if(ring.shouldProcess(timestamp.get))
          capsule.handleRequest(request) match {
            case Right(p) =>
              ring.add(reqid.get, timestamp.get, p)
              p.future onComplete {
                case TSuccess(r)            => upstream ! r.json
                case TFailure(NonFatal(ex)) => logger.error(s"Error occurred for request $request", ex)
              }
            case Left(_) =>
              logger.error(s"There is something seriously wrong if this is called for request = $request")
          }
        else {
          logger.error(s"Request cannot be processed as it is delayed $request")
          upstream ! Failure("Delayed request", GATEWAY_TIMEOUT, "Request cannot be processed as it is delayed").json
        }

      case _ =>
        logger.error(s"Request is invalid $request")
        upstream ! Failure("Invalid request", BAD_REQUEST, "Request is invalid").json
    }
  }

}