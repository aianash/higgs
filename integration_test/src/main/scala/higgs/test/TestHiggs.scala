package higgs.test

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Failure, Success}
import scala.util.control.NonFatal

import play.api.Play.current
import play.api.libs.ws._
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger

object TestHiggs {

  private val log = Logger(this.getClass)

  private val FBToken = "CAALYIU6LOQsBAEkQ6u9rDc0OzzFwY4XoBnioTvS4qW3kZAw7v1WoKQtkjhvxpWAvxZCwYASf8ZCW7nFhRGxcx1ybH5CF0EZAUGot3CKWZChtaGxOmJNcmm59UKE6LxEFiJ6EbC4F5zeORXZAXODohdiZADGZBCDxVGeCb61evsF80jho7hK9YeZCw6vY7GHgfJzRZACYwXAzSpyEW6DnuZBZB0GP"
  private val BASE_URL = "http://127.0.0.1:9000/v1/"


  def main(args: Array[String]) {

    val builder = new com.ning.http.client.AsyncHttpClientConfig.Builder()
    val client = new play.api.libs.ws.ning.NingWSClient(builder.build())

    val authInfo = Json.obj(
      "fbUserId" -> Json.obj("uuid" -> 10203676044758492L),
      "token"    -> FBToken,
      "clientId" -> "boson-app"
    )

    val aelapsed = lapse.Stopwatch.start()
    val tokenF =
      client.url(BASE_URL + "oauth/token").withHeaders("Content-Type" -> "text/json").post(authInfo).map {
        response =>
          (response.json \ "token").as[String]
      } andThen {
        case Failure(NonFatal(ex)) => log.error("Caught error while getting token", ex)
        case Success(token) => println(s"Received token = $token in ${aelapsed()}")
      }

    val userInfoF =
      tokenF flatMap { token =>
        val elapsed = lapse.Stopwatch.start()
        client.url(BASE_URL + "me")
              .withHeaders("Content-Type" -> "text/json")
              .withQueryString("accessToken" -> token)
              .get().map { response => response.json }
              .andThen {
                case Success(_) => println(s"Received user info in ${elapsed()}")
              }
      } andThen {
        case Failure(NonFatal(ex)) => log.error("Caught error while getting user info", ex)
        case Success(user) => println(s"Received user info = $user")
      }

    // val resultF =

  }

}