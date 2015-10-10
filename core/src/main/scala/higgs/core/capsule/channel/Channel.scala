package higgs.core.capsule.channel

import scala.collection.mutable.Map
import scala.concurrent.{promise, Promise}

import scalaz.{Success => _, _}, Scalaz._

import play.api.libs.json._

import higgs.core.capsule._

import neutrino.core.user._


trait Channel {
  def parent(parent: OneToOneChannel): Unit
  def sendMessage(msg: Message): Unit
  def +(another: Channel)(userId: UserId): Channel = {
    OneToOneChannel(userId).merging(this, another)
  }
}

class OneToOneChannel(val userId: UserId) extends Channel {

  private var parentO = none[OneToOneChannel]

  def parent(parent: OneToOneChannel) = this.parentO = parent.some

  def sendMessage(msg: Message): Unit =
    parentO.foreach(_.sendMessage(msg))

}

object OneToOneChannel {

  def apply(userId: UserId) =
    new {
      def merging(ch1: Channel, ch2: Channel): OneToOneChannel = {
        val ch = new OneToOneChannel(userId)
        ch1.parent(ch)
        ch2.parent(ch)
        ch
      }
    }

}

class ManyToOneChannel(hashifier: Hashifier[_], f: Any => Option[JsValue]) extends Channel {

  private var userId2channel = Map.empty[UserId, OneToOneChannel]
  private var req2promise    = Map.empty[Int, Promise[Response]]
  private var req2reqid      = Map.empty[Int, Int]

  def hashFor(request: Any) = hashifier.hash(request)

  def parent(channel: OneToOneChannel): Unit =
    userId2channel += (channel.userId -> channel)

  def sendResponse(response: Any, responseType: String): Unit = {
    val hash = hashFor(response)
    for {
      rid <- req2reqid.get(hash)
      js  <- f(response)
      p   <- req2promise.get(hash)
    } yield if(!p.isCompleted) p success Success(rid, responseType, js)
  }

  def sendMessage(msg: Message): Unit =
    userId2channel.get(msg.userId).map(_.sendMessage(msg))

  def registerRequest(parsedReq: Any, reqid: Int): Promise[Response] = {
    val hash = hashFor(parsedReq)
    val p = promise[Response]
    req2promise += (hash -> p)
    req2reqid += (hash -> reqid)
    p
  }

}