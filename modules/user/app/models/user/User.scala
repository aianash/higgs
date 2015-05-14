package models.user

import scala.util.Random

import com.goshoplane.common._

import scalaz._, Scalaz._

import play.api.libs.json._
import play.api.libs.functional.syntax._


/**
 * Json combinators i.e. Reads, Writes, and Format
 * for User related structures
 */
trait UserJsonCombinators extends models.auth.AuthJsonCombinators {

  // UserName
  protected implicit val usernameWrites: Writes[UserName] = (
    (__ \ "first") .writeNullable[String] ~
    (__ \ "last")  .writeNullable[String] ~
    (__ \ "handle").writeNullable[String]
  ) { name: UserName => (name.first, name.last, name.handle) }

  // Locale
  protected implicit val localeWrites: Writes[Locale] =
    Writes(locale => JsString(locale.name))

  // Gender
  protected implicit val genderWrites: Writes[Gender] =
    Writes(gender => JsString(gender.name))

  // FacebookInfo
  protected implicit val facebookInfoWrites: Writes[FacebookInfo] = (
    (__ \ "userId") .write[UserId] ~
    (__ \ "token")  .writeNullable[String]
  ) { info: FacebookInfo => (info.userId, info.token) }

  // UserAvatar
  protected implicit val userAvatarWrites: Writes[UserAvatar] = (
    (__ \ "small")  .writeNullable[String] ~
    (__ \ "medium") .writeNullable[String] ~
    (__ \ "large")  .writeNullable[String]
  ) { avatar: UserAvatar =>
      import avatar._
      (small, medium, large)
  }

  // UserInfo
  protected implicit val userInfoWrite: Writes[UserInfo] = (
    (__ \ "name")         .writeNullable[UserName] ~
    (__ \ "locale")       .writeNullable[Locale] ~
    (__ \ "gender")       .writeNullable[Gender] ~
    (__ \ "facebookInfo") .writeNullable[FacebookInfo] ~
    (__ \ "email")        .writeNullable[String] ~
    (__ \ "timezone")     .writeNullable[String] ~
    (__ \ "avatar")       .writeNullable[UserAvatar] ~
    (__ \ "isNew")        .writeNullable[Boolean]
  ) { info: UserInfo =>
      import info._
      (name, locale, gender, facebookInfo, email, timezone, avatar, isNew)
  }
}