package models.auth

import play.api.libs.json._
import play.api.libs.functional.syntax._

import neutrino.core.auth._
import neutrino.core.user._


/**
 * Json combinators i.e. Reads, Writes, and Format
 * for Auth related structures
 */
trait AuthJsonCombinators {

  // fb auth info
  protected implicit val fbAuthInfoFormat: Format[FBAuthInfo] = (
    (__ \ "fbauthtoken").format[String] and
    (__ \ "fbuid").format[String]
  ) ((token, id) =>
      FBAuthInfo(FBUserId(id.toLong), FBAuthToken(token)),
      (a: FBAuthInfo) => (a.authToken.value, a.fbUserId.id.toString)
    )

  // google auth info
  protected implicit val googleAuthInfoFormat: Format[GoogleAuthInfo] = (
    (__ \ "guid").format[String] and
    (__ \ "gauthtoken").format[String] and
    (__ \ "gidtoken").format[String]
  ) ((guid, token, idtoken) =>
      GoogleAuthInfo(GoogleUserId(guid), GoogleAuthToken(token), GoogleIdToken(idtoken)),
      (a: GoogleAuthInfo) => (a.googleUserId.id, a.authToken.value, a.idToken.value)
    )

}