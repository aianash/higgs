package actions.auth

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Try, Success, Failure}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc._, Results._
import play.api.Play.current
import play.api.Logger

import actors.auth._
import models.auth._

import scalaz._, Scalaz._

import goshoplane.commons.core.protocols._, Implicits._

import akka.util.Timeout


/**
 * Wrapped Request containing user object optionally
 */
case class AuthRequest[A](val user: Option[User], request: Request[A])
  extends WrappedRequest(request)


/**
 * Custom Authenticate action, the uses query param `accessToken`
 * to get user id and create [[actions.AuthRequest]].
 * [[actions.AuthRequest]] contains user if user id is successfully retrieved using token.
 */
object Authenticate extends ActionBuilder[AuthRequest] {

  val log = Logger(this.getClass)

  def invokeBlock[A](request: Request[A], block: (AuthRequest[A]) => Future[Result]) = {

    // Calling here, so that plugins get a chance to initialize
    val AuthService = Actors.authService

    val userFO =
      (for {
        token <- request.queryString.get("accessToken").flatMap(_.headOption)
      } yield {
        implicit val timeout = Timeout(1 seconds) // [TO DO] set a proper value, this is too much
                                                  // should never reach this much
        val userF = AuthService ?= VerifyTokenAndGetUser(token)
        userF.map(_.some) recover {
          case NonFatal(ex) =>
            log.error(s"Caught error [${ex.getMessage}] while verifying token and getting user", ex)
            None
        }
      }) getOrElse Future.successful(None) // when no access token altogether

    userFO flatMap(userO => block(AuthRequest(userO, request)))
  }

}


/**
 * This Action filter is used if action needs to be performed
 * only when [[actions.AuthRequest]] has valid user object
 */
object OnlyIfAuthenticated extends ActionFilter[AuthRequest] {
  def filter[A](input: AuthRequest[A]) = Future.successful {
    if(input.user.isEmpty) Some(Forbidden)
    else None
  }
}