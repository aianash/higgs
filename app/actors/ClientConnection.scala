package actors

import akka.actor.{Actor, ActorLogging, Props, ActorRef}

import javax.inject._

import com.google.inject.assistedinject.Assisted

import play.api.libs.json._

import higgs.user._
import higgs.core.capsule._

import neutrino.core.user._

class ClientConnection @Inject() (@Assisted upstream: ActorRef) extends Actor with ActorLogging {

  import context._

  // private var user: UserCapsule = new UserCapsule


  // val process = (VoidCapsule +> VoidCapsule)(UserId(98765456789L))(upstream)

  def receive = {
    // case req: Request => process.process(req)

    case _ =>
  }

}

/**
 * Companion object
 */
object ClientConnection {

  trait Factory {
    def apply(upstream: ActorRef): Actor
  }

}