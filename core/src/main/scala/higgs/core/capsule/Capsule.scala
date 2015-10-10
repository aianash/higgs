package higgs.core.capsule

import scala.concurrent.Promise

import scalaz._, Scalaz._

import akka.actor.ActorRef

import neutrino.core.user._

import higgs.core.capsule.channel._


/**
 * Example:
 * {{{
 *   case class UserId(uuid: Long)
 *   case class SearchId(sruid: Long)
 *   case class GetUser(userId: UserId)
 *   case class UserDetail(userId: UserId)
 *   case class SearchRequest(userId: UserId, searchId: SearchId)
 *
 *   object User {
 *     implicit val UserHashifier = new Hashifier[UserCapsule] {
 *
 *       implicit val userIdH = Hash.by[UserId, Long](_.uuid)
 *       implicit val searchIdH = Hash.by[SearchId, Long](_.sruid)
 *
 *       val getUserH = Hash.by[GetUser, UserId](_.userId)
 *       val userDetailH = Hash.by[UserDetail, UserId](_.userId)
 *
 *       implicit val userSearchIdH = Hash.tuple1[UserId, SearchId]
 *       val searchReqH = Hash.by[SearchRequest, (UserId, SearchId)](s => s.userId -> s.searchId)
 *
 *       def hashAny = getUserH :: userDetailH :: searchReqH
 *     }
 *   }
 *
 *   import User._
 *
 *   class UserCapsule extends Capsule[UserCapsule] {
 *     def hashForRespnse: String = ???
 *     def parseRequest(request: Request): Option[Any] = ???
 *     def processRequest(request: Any): Unit = ???
 *     def responseToJson(resp: Response): JsObject = ???
 *   }
 * }}}
 */

trait Capsule {

  private[capsule] def channel: Option[Channel]
  def handleRequest: Request => Either[Request, Promise[Response]]
  def +>(another: Capsule)(userId: UserId): Capsule =
    CompositeCapsule(this, another, userId)

  def apply(upstream: ActorRef) = {
    channel.foreach {
      case o: OneToOneChannel => o.upstream(upstream)
      case _ =>
    }
    new Process(this, upstream)
  }

}

object VoidCapsule extends Capsule {

  val channel = VoidChannel.some

  def handleRequest = (request: Request) =>
    Right(Promise.failed[Response](new Exception("No capsule to handle the request")))

}