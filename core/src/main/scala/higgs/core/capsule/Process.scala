package higgs.core.capsule

import scala.concurrent.ExecutionContext

import akka.actor.ActorRef

import play.api.libs.json._
import play.api.http.Status._

class Process(capsule: Capsule, upstream: ActorRef) {

  private val maxActiveReq = 4
  private val ring = new Ring(maxActiveReq)

  def process(request: Request)(implicit ec: ExecutionContext): Unit = {
    import request._
    import RequestType._

    if(reqType equals POST) capsule.handleRequest(request)
    else if((reqType equals GET) && reqid.nonEmpty && timestamp.nonEmpty)
      if(ring.shouldProcess(timestamp.get)) {
        capsule.handleRequest(request) match {
          case Right(p) =>
            ring.add(reqid.get, timestamp.get, p)
            p.future onSuccess {
              case r => upstream ! r.json
            }

          case Left(_) =>
        }
      } else upstream ! Failure("Delayed request", GATEWAY_TIMEOUT, "Request cannot be processed as it is delayed").json

    else upstream ! Failure("Invalid request", BAD_REQUEST, "Request is invalid").json
  }

}