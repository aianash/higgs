package models.auth

import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.goshoplane.common._

/**
 * FBAuthInfo (parsed from request body of post /v1/oauth/token)
 * @param   fbUserId      user's fb uuid
 * @param   token         fb's long lived access token
 * @param   clientId      client id of the requester
 */
case class FBAuthInfo(fbUserId: UserId, token: String, clientId: String)


/**
 * Json combinators i.e. Reads, Writes, and Format
 * for Auth related structures
 */
trait AuthJsonCombinators {

  // user id
  protected val userIdReads: Reads[UserId] =
    (__ \ "uuid").read[String].map(id => UserId(id.toLong))

  protected val userIdWrites: Writes[UserId] =
    (__ \ "uuid").write[String].contramap[UserId](_.uuid.toString)

  protected implicit val userIdFormat: Format[UserId] =
    Format(userIdReads, userIdWrites)

  // read for FBAuthInfo
  protected val fbAuthInfoReads = Json.reads[FBAuthInfo]
  protected val fbAuthInfoWrites = Json.writes[FBAuthInfo]
  protected implicit val fbAuthInfoFormat: Format[FBAuthInfo] =
    Format(fbAuthInfoReads, fbAuthInfoWrites)
}