package actors.feed

import scala.concurrent.Future
import scala.util.Failure
import scala.util.control.NonFatal

import akka.actor.{Actor, Props, ActorLogging}
import akka.pattern.pipe

import com.goshoplane.common._
import com.goshoplane.neutrino.service._
import com.goshoplane.neutrino.feed._

import com.twitter.bijection._, Conversion.asMethod
import com.twitter.bijection.twitter_util.UtilBijections._

import goshoplane.commons.core.protocols._


/**
 * This Actor acts as a client to Neutrino Service's Feed apis
 */
class FeedClient(neutrino: Neutrino$FinagleClient) extends Actor with ActorLogging {

  import context.dispatcher

  def receive = {

    // get common feed
    case GetCommonFeed(filter) =>
      neutrino.getCommonFeed(filter).as[Future[Feed]]
      .andThen {
        case Failure(NonFatal(ex)) =>
          log.error(ex, "Caught error [{}] while getting common feed with filter = [{}]",
                        ex.getMessage,
                        filter)

      } pipeTo sender()

    // get user feed
    case GetUserFeed(userId, filter) =>
      neutrino.getUserFeed(userId, filter).as[Future[Feed]]
      .andThen {
        case Failure(NonFatal(ex)) =>
          log.error(ex, "Caught error [{}] while getting user feed for user = {} with filter = [{}]",
                        ex.getMessage,
                        userId.uuid,
                        filter)
      } pipeTo sender()
  }

}


object FeedClient {
  def props(neutrino: Neutrino$FinagleClient) = Props(new FeedClient(neutrino))
}


sealed trait FeedClientProtocols
case class GetCommonFeed(filter: FeedFilter) extends FeedClientProtocols with Replyable[Feed]
case class GetUserFeed(userId: UserId, filter: FeedFilter) extends FeedClientProtocols with Replyable[Feed]