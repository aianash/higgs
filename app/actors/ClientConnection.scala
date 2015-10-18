package actors

import scala.util.control.NonFatal

import akka.actor.{Actor, ActorLogging, Props, ActorRef}

import javax.inject._

import com.google.inject.assistedinject.Assisted

import play.api.libs.json._
import play.api.Logger

import higgs.core.capsule._
import higgs.search.SearchCapsule

import neutrino.core.user._


class ClientConnection @Inject() (@Assisted userId: UserId, @Assisted upstream: ActorRef) extends Actor with ActorLogging {

  import context._

  private var search = SearchCapsule(context.system)

  val process = (search +> VoidCapsule)(userId)(upstream)

  log.info(s"Created a new connection for user $userId")

  def receive = {
    case req: Request =>
      try process.process(req.copy(userId = userId))
      catch {
        case NonFatal(ex) => log.error(ex, s"Exception while processing the request $req")
      }
    case _ =>
  }

  override def postStop(): Unit = {
    log.info(s"Connection stopped for user $userId")
  }

}

/**
 * Companion object
 */
object ClientConnection {

  trait Factory {
    def apply(userId: UserId, upstream: ActorRef): Actor
  }

}