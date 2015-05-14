package actors.shopplan

import scala.concurrent.Future
import scala.util.Failure
import scala.util.control.NonFatal

import akka.actor.{Actor, Props, ActorLogging}
import akka.pattern.pipe

import com.goshoplane.common._
import com.goshoplane.neutrino.service._
import com.goshoplane.neutrino.shopplan._

import com.twitter.bijection._, Conversion.asMethod
import com.twitter.bijection.twitter_util.UtilBijections._

import goshoplane.commons.core.protocols._


/**
 * This Actor acts as a client to Neutrino service for user apis
 */
class ShopPlanClient(neutrino: Neutrino$FinagleClient) extends Actor with ActorLogging {

  import context.dispatcher

  def receive = {

    // get shop plan
    case GetShopPlan(shopplanId, fields) =>
      neutrino.getShopPlan(shopplanId, fields).as[Future[ShopPlan]]
      .andThen {
        case Failure(NonFatal(ex)) =>
          log.error(ex, "Caught error [{}] while getting shopplan {}.{} for fields = [{}]",
                        ex.getMessage,
                        shopplanId.createdBy.uuid,
                        shopplanId.suid,
                        fields)
      } pipeTo sender()


    // get own shop plans fot the user
    case GetOwnShopPlans(userId, fields) =>
      neutrino.getOwnShopPlans(userId, fields).as[Future[Seq[ShopPlan]]]
      .andThen {
        case Failure(NonFatal(ex)) =>
          log.error(ex, "Caught error [{}] while getting own shopplans for user = {} and fields = [{}]",
                        ex.getMessage,
                        userId.uuid,
                        fields)
      } pipeTo sender()


    // create a new shop plan
    case CreateShopPlan(userId, cud) =>
      neutrino.createShopPlan(userId, cud).as[Future[ShopPlanId]]
      .andThen {
        case Failure(NonFatal(ex)) =>
          log.error(ex, "Caught error [{}] while creating shopplans for user = {}",
                        ex.getMessage,
                        userId.uuid)
      } pipeTo sender()


    // end shop plan
    case EndShopPlan(shopplanId) =>
      neutrino.endShopPlan(shopplanId).as[Future[Boolean]]
      .andThen {
        case Failure(NonFatal(ex)) =>
          log.error(ex, "Caught error [{}] while ending shopplan {}.{}",
                        ex.getMessage,
                        shopplanId.createdBy.uuid,
                        shopplanId.suid)
      } pipeTo sender()

  }

}


object ShopPlanClient {
  def props(neutrino: Neutrino$FinagleClient) = Props(new ShopPlanClient(neutrino))
}


sealed trait ShopPlanClientProtocol
case class GetShopPlan(shopplanId: ShopPlanId, fields: Seq[ShopPlanField]) extends ShopPlanClientProtocol with Replyable[Seq[ShopPlan]]
case class GetOwnShopPlans(userId: UserId, fields: Seq[ShopPlanField]) extends ShopPlanClientProtocol with Replyable[ShopPlan]
case class CreateShopPlan(userId: UserId, cud: CUDShopPlan) extends ShopPlanClientProtocol with Replyable[ShopPlanId]
case class EndShopPlan(shopplanId: ShopPlanId) extends ShopPlanClientProtocol with Replyable[Boolean]