package higgs.core.capsule

import scala.concurrent.Promise
import scala.math.Ordering
import scala.collection.SortedSet

import play.api.Logger


class Ring(maxActiveReq: Int) {

  val logger = Logger(getClass)

  private val size = maxActiveReq * 2
  private var ring = Array.ofDim[Promise[Response]](size)

  private var ts2rid = SortedSet.empty[(Long, Int)]

  def add(reqid: Int, timestamp: Long, p: Promise[Response]): Unit = {
    logger.info(s"Adding request for $reqid received with timestamp = $timestamp")
    ring(reqid) = p
    ts2rid += ((timestamp, reqid))
    cleanup
  }

  def isActive(index: Int): Boolean = (ring(index) != null)

  def shouldProcess(timestamp: Long) =
    (ts2rid.size < maxActiveReq || timestamp > ts2rid.head._1)

  private def cleanup: Unit = {
    ts2rid.dropRight(maxActiveReq) foreach { t =>
      logger.info(s"Dropping request with reqid = ${t._2} received at timestamp = ${t._1}")
      if(ring(t._2) != null) {
        ring(t._2).failure(new Exception("Request timed out for request"))
        ring(t._2) = null
      }
    }
    ts2rid = ts2rid.takeRight(maxActiveReq)
  }

}

object Ring {
  def apply(maxActiveReq: Int) = new Ring(maxActiveReq)
}