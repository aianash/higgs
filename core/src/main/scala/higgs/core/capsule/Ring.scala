package higgs.core.capsule

import scala.concurrent.Promise

class Ring(maxActiveReq: Int) {

  val size = maxActiveReq * 2
  var ring = Array.ofDim[Promise[Response]](size)
  var current: Int = 0

  def add(p: Promise[Response]): Unit = {
    ring(current) = p
    current = (current + 1) % size
    cleanup((size + current - maxActiveReq) % size)
  }

  def cleanup(index: Int): Unit = {
    if(ring(index) != null) {
      ring(index).failure(new Exception("Request timed out"))
      ring(index) = null
    }
  }

  def isActive(index: Int): Boolean = (ring(index) != null)

}

object Ring {
  def apply(maxActiveReq: Int) = new Ring(maxActiveReq)
}