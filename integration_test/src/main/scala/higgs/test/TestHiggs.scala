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

  private val FBToken = "CAALYIU6LOQsBAIiSgVkgjIVCBQZAaUu78nZCPp9jUorC8ZADuzgRchyc3rr6f2dTeTqYi0SOjyxtHx08ZCoRPeBPDdAxhFecirbgunTD5jPU8Yo3PlXODywZBQ0ZC1RYQfzW688hInR5pCt6ZBGI357ZCdqYEPk4o38bKLeUyRCkAkfrc6YCWiSGXXsjIJf5vfpIsHzFaj12SWTVd9lpL3QvJHFkVlNsrXbh10jE4aWSiQZDZD"
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

    val resultF =
      tokenF flatMap { token =>
        val elapsed = lapse.Stopwatch.start()
        val query = Json.obj(
          "queryText" -> "levis men's jeans",
          "pageIndex" -> 1,
          "pageSize"  -> 10
        )

        client.url(BASE_URL + "search/2999388278893")
              .withHeaders("Content-Type" -> "text/json")
              .withQueryString("accessToken" -> token)
              .post(query).map { response => response.json }
              .andThen {
                case Failure(NonFatal(ex)) => log.error("Caught error while searching", ex)
                case Success(result) =>
                  println(s"Got result in ${elapsed()}")
                  println(Json.prettyPrint(result))
              }
      }

      val cudF =
        (for {
          token  <- tokenF
          result <- resultF
        } yield {
          val itemIds = (result \ "result" \\ "itemId")
          val cud = Json.obj(
            "adds" -> itemIds
          )

          val elapsed = lapse.Stopwatch.start()
          client.url(BASE_URL + "bucket/cud")
                .withHeaders("Content-Type" -> "text/json")
                .withQueryString("accessToken" -> token)
                .post(cud).map { response => response.json }
                .andThen {
                  case Failure(NonFatal(ex)) => log.error("Caught error while searching", ex)
                  case Success(result) =>
                    println(s"CUD bucket in ${elapsed()}")
                    println(Json.prettyPrint(result))
                }
                .map(json => (json \ "success").as[Boolean])
        }).flatMap(identity)

      val storesF =
        for {
          token <- tokenF
          success <- cudF if success
        } yield {
          val elapsed = lapse.Stopwatch.start()
          client.url(BASE_URL + "bucket/stores?fields=Name,Address,ItemTypes,CatalogueItemIds")
                .withHeaders("Content-Type" -> "text/json")
                .withQueryString("accessToken" -> token)
                .get().map { response => response.json }
                .andThen {
                  case Failure(NonFatal(ex)) => log.error("Caught error while getting bucket stores")
                  case Success(result) =>
                    println(s"Get stores in ${elapsed()}")
                    println(Json.prettyPrint(result))
                }
        }

  }

}