package actors.auth

import javax.inject._

import play.api._
import play.api.libs.concurrent.Akka
import play.api.inject._

import neutrino.auth._


/**
 * Companion object, primarily to access [[akka.actor.ActorRef]] of
 * client actors
 */
object Actors {

  private def actors(implicit app: Application) = app.plugin[Actors]
    .getOrElse(sys.error("Actors plugin not registered"))

  def authService(implicit app: Application) = actors.authService

}


/**
 * All service client and client actors to these service's are
 * creating using Play Plugin. This plugin's initialization order
 * is specified in `play.plugins` file
 */
class Actors @Inject() (app: Application) extends Plugin {

  private def system = Akka.system(app)

  private val neutrinoAuth = system.actorOf(AuthenticationSupervisor.props)

  private lazy val authService  = system.actorOf(AuthService.props(neutrinoAuth), "authService")

}