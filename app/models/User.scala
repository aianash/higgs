package models

import scala.util.Random

import com.goshoplane.common._

import scalaz._, Scalaz._


/**
 * User object currently just holding user id
 *
 * @param id    UserId of user
 */
case class User(id: UserId)


// Companion object
object User {

  /**
   * Decrypt access token to get user id
   * [TO IMPLEMENT]
   * @param token      token string
   */
  def fromAccessToken(token: String) =
    User(UserId(Random.nextLong)).some

}