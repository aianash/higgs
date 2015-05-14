package actors.bucket

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
 * This Actor acts as a client to Neutrino Service's bucket apis
 */
class BucketClient(neutrino: Neutrino$FinagleClient) extends Actor with ActorLogging {

  import context.dispatcher

  def receive = {

    // get bucket stores
    case GetBucketStores(userId, fields) =>
      neutrino.getBucketStores(userId, fields).as[Future[Seq[BucketStore]]]
      .andThen {
        case Failure(NonFatal(ex)) =>
          log.error(ex, "Caught error [{}] while getting bucket stores for user = {}",
                        ex.getMessage,
                        userId.uuid)
      } pipeTo sender()


    // create/update/delete bucket
    case ModifyBucket(userId, cud) =>
      neutrino.cudBucket(userId, cud).as[Future[Boolean]]
      .andThen {
        case Failure(NonFatal(ex)) =>
          log.error(ex, "Caught error [{}] while performing create/update/delete on bucket for user = {}",
                        ex.getMessage,
                        userId.uuid)
      } pipeTo sender()

  }

}

object BucketClient {
  def props(neutrino: Neutrino$FinagleClient) = Props(new BucketClient(neutrino))
}


sealed trait BucketClientProtocol
case class GetBucketStores(userId: UserId, fields: Seq[BucketStoreField])
  extends BucketClientProtocol with Replyable[Seq[BucketStore]]
case class ModifyBucket(userId: UserId, cud: CUDBucket)
  extends BucketClientProtocol with Replyable[Boolean]