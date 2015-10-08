package higgs.core.capsule

import scala.concurrent.ExecutionContext

import akka.actor.ActorRef

class Process(capsule: Capsule, upstream: ActorRef) {

  private val maxActiveReq = 4
  private val ring = new Ring(maxActiveReq)

  def process(request: Request)(implicit ec: ExecutionContext): Unit = {
    val p = capsule.handleRequest(request).right.get
    ring.add(p)
    p.future onSuccess {
      case r => upstream ! r
    }
  }

}