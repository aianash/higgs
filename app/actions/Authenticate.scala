package actions

import scala.concurrent.Future

import play.api.mvc._, Results._

import models.User


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

  def invokeBlock[A](
    request: Request[A],
    block: (AuthRequest[A]) => Future[Result]) = {

    val user =
      for {
        token <- request.queryString.get("accessToken").flatMap(_.headOption)
        user  <- User.fromAccessToken(token)
      } yield user

    block(AuthRequest(user, request))
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