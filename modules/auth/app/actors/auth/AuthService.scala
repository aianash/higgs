package actors.auth

import scala.util.control.NonFatal
import scala.collection.JavaConversions._
import scala.concurrent.Future
import scala.util._

import java.io._
import java.security._
import java.security.spec._
import java.security.interfaces.{RSAPrivateKey, RSAPublicKey}

import akka.actor.{Actor, Props, ActorLogging}
import akka.pattern.pipe

import models.auth._

import org.jose4j.jwt.consumer.{InvalidJwtException, JwtConsumerBuilder, JwtConsumer}
import org.jose4j.jwt._
import org.jose4j.jws._
import org.jose4j.jwk._

import scalaz.Scalaz._

import com.twitter.bijection._, twitter_util.UtilBijections._
import com.twitter.bijection.Conversion.asMethod

import play.api.Logger
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

import goshoplane.commons.core.protocols._

import com.goshoplane.neutrino.service._
import com.goshoplane.common._

import com.restfb._, exception._
import com.restfb.batch._, BatchRequest._


case class InvalidCredentialsException(message: String) extends Exception

sealed trait AuthServiceProtocol
case class VerifyAndGetTokenFor(authInfo: FBAuthInfo) extends AuthServiceProtocol with Replyable[String]
case class VerifyTokenAndGetUser(token: String) extends AuthServiceProtocol with Replyable[User]


/**
 * This actor is used for authentication related tasks like
 * - verifying user's fb token, fetching user info from FB, updating to data storage and creating
 *   a higgs access token
 * - verfying access token and create user with user id of the authenticated token
 */
class AuthService(neutrino: Neutrino$FinagleClient) extends Actor with ActorLogging {

  import context.dispatcher
  import AuthService._

  def receive = {

    /**
     * - Verify and get user info using face token in fbAuthInfo
     * - Create/Update a new user in neutrino
     * - Generate an new higgs access token for ther user
     * - Send back the token
     */
    case VerifyAndGetTokenFor(fbAuthInfo) =>
      val tokenF =
        for {
          userInfo <- getUsersFacebookInfo(fbAuthInfo)
          userId   <- asMethod(neutrino.createUser(userInfo)).as[Future[UserId]]
        } yield generateToken(userId, fbAuthInfo.clientId)


      tokenF andThen {
        case Failure(NonFatal(ex)) =>
          log.error(ex, "Caught error [{}] while verifying, updating user and creating token for info = {}",
                        ex.getMessage,
                        fbAuthInfo)
      }

      tokenF pipeTo sender()

    /**
     * - Verify higgs access token and extract claims
     * - Create User from uuid in claims
     */
    case VerifyTokenAndGetUser(token) =>
      try {
        val claims = getClaimsFromToken(token, "boson-app")
        val uuid   = claims.getClaimValue("uuid", classOf[java.lang.Long])
        sender() ! User(UserId(uuid))
      } catch {
        case NonFatal(ex) =>
          log.error(ex, "Caught error [{}] while getting claims and user from token",
                        ex.getMessage)
          sender() ! akka.actor.Status.Failure(ex)
      }

  }


  /**
   * - Veriy facebook token for user by calling "/me" facebook api
   * - Fill UserInfo with user's information fetched from facebook apis "/me" and "/me/picture"
   *
   * @param fbAuthInfo        facebook auth info containing token
   * @return                  UserInfo with filled in values
   */
  private def getUsersFacebookInfo(fbAuthInfo: FBAuthInfo) = Future {

    val fbClient = new DefaultFacebookClient(fbAuthInfo.token, fbAppSecret, Version.VERSION_2_0)
    val me = new BatchRequestBuilder("me").build()
    val picture = new BatchRequestBuilder("me/picture?redirect=false").build()

    try {
      val responses = fbClient.executeBatch(me, picture)

      val mejson      = Json.parse(responses.get(0).getBody)
      val picturejson = Json.parse(responses.get(1).getBody)

      val name = UserName(
        first  = (mejson \ "first_name").asOpt[String],
        last   = (mejson \ "last_name").asOpt[String],
        handle = (mejson \ "name").asOpt[String])

      val avatar = UserAvatar(
        small  = (picturejson \ "url").asOpt[String],
        medium = (picturejson \ "url").asOpt[String],
        large  = (picturejson \ "url").asOpt[String])

      val facebookInfo = FacebookInfo(
        userId = UserId((mejson \ "id").as[String].toLong),
        token  = fbAuthInfo.token.some)

      UserInfo(
        name         = name.some,
        facebookInfo = facebookInfo.some,
        avatar       = avatar.some,
        locale       = (mejson \ "locale").asOpt[String].flatMap(l => Locale.valueOf(l.toUpperCase)),
        gender       = (mejson \ "gender").asOpt[String].flatMap(g => Gender.valueOf(g.toUpperCase)),
        email        = (mejson \ "email").asOpt[String],
        timezone     = (mejson \ "timezone").asOpt[String],
        isNew        = Some(true)
      )
    } catch {
      case ex: FacebookOAuthException =>
        log.error(ex, "Caught facebook auth error [{}] while gettting user's facebook info",
                      ex.getMessage)
        throw InvalidCredentialsException("Couldnt authenticate facebook token")

      case NonFatal(ex) =>
        log.error(ex, "Caught error [{}] while gettting user's facebook info",
                      ex.getMessage)
        throw new Exception("Error while getting user's facebook information")
    }
  }


  /**
   * Generate token with payload contains claims for given uuid
   * for the given audience
   *
   * @param   userId      UserId used in payload
   * @param   audience    client if for which token is being generated
   * @return              Token string
   */
  private def generateToken(userId: UserId, audience: String) = {
    // Create the Claims, which will be the content of the JWT
    val claims = new JwtClaims()
    claims.setIssuer(issuer)                            // who creates the token and signs it
    claims.setAudience(audience)                        // to whom the token is intended to be sent
    claims.setExpirationTimeMinutesInTheFuture(43200)   // time when the token will expire (30 days from now)
    claims.setGeneratedJwtId()                          // a unique identifier for the token
    claims.setIssuedAtToNow()                           // when the token was issued/created (now)
    claims.setNotBeforeMinutesInThePast(2)              // time before which the token is not yet valid (2 minutes ago)
    claims.setSubject("user")                           // the subject/principal is whom the token is about
    claims.setClaim("uuid", userId.uuid)                // additional claims/attributes about the subject can be added
    val apis = List("all")
    claims.setStringListClaim("apis", apis) // multi-valued claims work too and will end up as a JSON array

    // A JWT is a JWS and/or a JWE with JSON claims as the payload.
    // In this example it is a JWS so we create a JsonWebSignature object.
    val jws = new JsonWebSignature()

    // The payload of the JWS is JSON content of the JWT Claims
    jws.setPayload(claims.toJson())

    // The JWT is signed using the private key
    jws.setKey(rsaJsonWebKey.getPrivateKey())

    // Set the Key ID (kid) header because it's just the polite thing to do.
    // We only have one key right now but a using a Key ID helps
    // facilitate a smooth key rollover process
    jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId())

    // Set the signature algorithm on the JWT/JWS that will protect the integrity of claims
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256)

    // Sign the JWS and produce the compact serialization or the complete JWT/JWS
    // representation, which is a string consisting of three dot ('.') separated
    // base64url-encoded parts in the form Header.Payload.Signature
    val token = jws.getCompactSerialization()

    token
  }


  /**
   * Verify token string and get claims from the token
   *
   * @param token       token string
   * @return            jwt claims from the token
   */
  def getClaimsFromToken(token: String, audience: String) = {
    // Use JwtConsumerBuilder to construct an appropriate JwtConsumer, which will
    // be used to validate and process the JWT.
    // The specific validation requirements for a JWT are context dependent, however,
    // it typically advisable to require a expiration time, a trusted issuer, and
    // and audience that identifies your system as the intended recipient.
    val jwtConsumer = new JwtConsumerBuilder()
            .setRequireExpirationTime() // the JWT must have an expiration time
            .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
            .setRequireSubject() // the JWT must have a subject claim
            .setExpectedIssuer(issuer) // whom the JWT needs to have been issued by
            .setExpectedAudience(audience) // to whom the JWT is intended for
            .setVerificationKey(publicKey) // verify the signature with the public key
            .build()

    try {
      jwtConsumer.processToClaims(token)
    } catch {
      case ex: InvalidJwtException =>
        // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
        // Hopefully with meaningful explanations(s) about what went wrong.
        log.error(ex, "Caught error [{}] while processing token to claims",
                      ex.getMessage)
        throw InvalidCredentialsException("Auth Token is invalid")
    }

  }

}


// Companion object
object AuthService {

  def props(neutrino: Neutrino$FinagleClient) = Props(new AuthService(neutrino))

  private val log = Logger(this.getClass)

  private val fbAppSecret = current.configuration.getString("fb.app-secret")
                                                 .getOrElse(sys.error("Couldn't find fb.app-secret"))
  private val fbAppId = current.configuration.getString("fb.app-id")
                                             .getOrElse(sys.error("Couldn't find fb.app-id"))
  private val issuer = current.configuration.getString("authenticate.issuer")
                                            .getOrElse(sys.error("Couldn't find authenticate.issuer"))
  private val keyId = current.configuration.getString("authenticate.key-id")
                                           .getOrElse(sys.error("Couldn't find authenticate.key-id"))

  /**
   * Private RSA key
   *
   * How to generate private key
   * {{{
   * // generate a 2048-bit RSA private key
   * $ openssl genrsa -out private_key.pem 2048
   *
   * // Convert private Key to PKCS#8 format (so Java can read it)
   * $ openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt
   *
   * }}}
   *
   */
  private val privateKey = try {
    val filename = current.configuration.getString("authenticate.private-key")
                                        .getOrElse(sys.error("Couldn't find private key file"))
    val f = new File(filename)
    val fis = new FileInputStream(f)
    val dis = new DataInputStream(fis)
    val keyBytes = Array.ofDim[Byte](f.length.asInstanceOf[Int])
    dis.readFully(keyBytes)
    dis.close

    val spec = new PKCS8EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    keyFactory.generatePrivate(spec).asInstanceOf[RSAPrivateKey]
  } catch {
    case NonFatal(ex) =>
      log.error(s"Caught error [${ex.getMessage}] while getting private key", ex)
      throw ex // Log and throw it back
  }


  /**
   * Public RSA Key
   *
   * How to generate public key
   * {{{
   * // Output public key portion in DER format (so Java can read it)
   * $ openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der
   * }}}
   *
   */
  private val publicKey = try {
    val filename = current.configuration.getString("authenticate.public-key")
                                        .getOrElse(sys.error("Couldn't find private key file"))
    val f = new File(filename)
    val fis = new FileInputStream(f)
    val dis = new DataInputStream(fis)
    val keyBytes = Array.ofDim[Byte](f.length.asInstanceOf[Int])
    dis.readFully(keyBytes)
    dis.close

    val spec = new X509EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    keyFactory.generatePublic(spec).asInstanceOf[RSAPublicKey]
  } catch {
    case NonFatal(ex) =>
      log.error(s"Caught error [${ex.getMessage}] while getting public key", ex)
      throw ex // Log and throw it back
  }


  // Holds public and private key and also the key id
  private val rsaJsonWebKey = new RsaJsonWebKey(publicKey)
  rsaJsonWebKey.setPrivateKey(privateKey)
  rsaJsonWebKey.setKeyId(keyId)


}