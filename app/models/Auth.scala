package models

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

trait AuthJsonCombinators {

  // user id
  protected val userIdReads: Reads[UserId] =
    (__ \ "uuid").read[Long].map(UserId(_))

  protected val userIdWrites: Writes[UserId] =
    (__ \ "uuid").write[Long].contramap[UserId](_.uuid)

  protected implicit val userIdFormat: Format[UserId] =
    Format(userIdReads, userIdWrites)

  // read for FBAuthInfo
  protected implicit val fbAuthInfoReads = Json.reads[FBAuthInfo]

}