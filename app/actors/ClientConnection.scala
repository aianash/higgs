package actors

import akka.actor.{Actor, ActorLogging, Props, ActorRef}

import javax.inject._

import com.google.inject.assistedinject.Assisted

import play.api.libs.json._

import higgs.core.capsule._
import higgs.search.SearchCapsule

import neutrino.core.user._


class ClientConnection @Inject() (@Assisted userId: UserId, @Assisted upstream: ActorRef) extends Actor with ActorLogging {

  import context._

  private var search = SearchCapsule(context.system)

  val process = (search +> VoidCapsule)(userId)(upstream)

  def receive = {
    case req: Request => process.process(req.copy(userId = userId))
    case _ =>
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