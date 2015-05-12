package actors

import scala.concurrent.Future
import scala.util.Failure
import scala.util.control.NonFatal

import akka.actor.{Actor, Props, ActorLogging}
import akka.pattern.pipe

import com.goshoplane.common._
import com.goshoplane.neutrino.service._

import com.twitter.bijection._, Conversion.asMethod
import com.twitter.bijection.twitter_util.UtilBijections._

import goshoplane.commons.core.protocols._


/**
 * This Actor acts as a client to Neutrino service for user apis
 */
class UserClient(neutrino: Neutrino$FinagleClient) extends Actor with ActorLogging {

  import context.dispatcher

  def receive = {

    // get user's info (complete info)
    case GetUserInfo(userId) =>
      neutrino.getUserDetail(userId).as[Future[UserInfo]]
      .andThen {
        case Failure(NonFatal(ex)) =>
          log.error(ex, "Caught error [{}] while getting user info for user id = {}",
                        ex.getMessage,
                        userId.uuid)
      } pipeTo sender()
  }

}


object UserClient {
  def props(neutrino: Neutrino$FinagleClient) = Props(new UserClient(neutrino))
}


sealed trait UserClientProtocol
case class GetUserInfo(userId: UserId) extends UserClientProtocol with Replyable[UserInfo]