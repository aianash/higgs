package actions.auth

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Try, Success, Failure}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject._

import play.api.mvc._, Results._
import play.api.Play.current
import play.api.Logger

import neutrino.core.user._

import actors.auth._
import models.auth._

import scalaz.Scalaz._

import goshoplane.commons.core.protocols._, Implicits._

import neutrino.core.user._

import akka.util.Timeout
import akka.actor.ActorRef


/**
 * Custom Authenticate action, the uses query param `accessToken`
 * to get user id and create [[actions.AuthRequest]].
 * [[actions.AuthRequest]] contains user if user id is successfully retrieved using token.
 */
@Singleton
class Authenticate @Inject() (@Named("auth-service") authService: ActorRef) {

  val log = Logger(this.getClass)

  def verify(request: RequestHeader): Future[Option[UserId]] =
    (for {
      token <- request.queryString.get("accessToken").flatMap(_.headOption)
    } yield {
      implicit val timeout = Timeout(1 seconds) // [TO DO] set a proper value, this is too much
                                                // should never reach this much
      val userIdF = authService ?= VerifyTokenAndGetUser(token)
      userIdF.map(_.some) recover {
        case NonFatal(ex) =>
          log.error(s"Caught error [${ex.getMessage}] while verifying token and getting user", ex)
          None
      }
    }).getOrElse(Future.successful(None)) // when no access token altogether

}