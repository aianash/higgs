package higgs.test

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Failure, Success}
import scala.util.control.NonFatal
import scala.concurrent.duration.FiniteDuration
import scala.collection.JavaConversions._

import java.util.concurrent.ConcurrentHashMap

import play.api.Play.current
import play.api.libs.ws._
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger

object TestHiggs {

  private val log = Logger(this.getClass)

  private val FBToken = "CAALYIU6LOQsBAAkp3KleNNdAA61rrwyZBth92ZC4fzkxjAdZAVI1s7gM6yIdSwnqehqWBm3gDRODuTI3jJvXf3Am2gYZC3jLobIEq9Ta6ZAFiEKEwsZBZB0IrjqGYedhmoTIcLVzY8OD5Q0pwtN1EzEvqr96olcvg5INCZBVrvenZBFZAVVhIVTtNev3dZCgCmCuaxadkx2jxmVl8bMJZBmaVSaBGL4iZBvcpU32CD5b2YefmOwZDZD"
  private val BASE_URL = "http://127.0.0.1:9000/v1/"


  def main(args: Array[String]) {

    val builder = new com.ning.http.client.AsyncHttpClientConfig.Builder()
    val client = new play.api.libs.ws.ning.NingWSClient(builder.build())

    var timeRecord = new ConcurrentHashMap[String, FiniteDuration]()

    val authInfo = Json.obj(
      "fbUserId" -> Json.obj("uuid" -> 10203676044758492L),
      "token"    -> FBToken,
      "clientId" -> "boson-app"
    )

    /**
     * Using facebook token authenticate higgs service and
     * get an access token to use further
     */
    val aelapsed = lapse.Stopwatch.start()
    val tokenF =
      client.url(BASE_URL + "oauth/token").withHeaders("Content-Type" -> "text/json").post(authInfo).map {
        response =>
          (response.json \ "token").as[String]
      } andThen {
        case Failure(NonFatal(ex)) => log.error("Caught error while getting token", ex)
        case Success(token) =>
          timeRecord.put("Received token", aelapsed())
          println(s"Received token = $token in ${aelapsed()}")
      }


    /**
     * Get user detail using the access token
     */
    val userInfoF =
      tokenF flatMap { token =>
        val elapsed = lapse.Stopwatch.start()
        client.url(BASE_URL + "me")
              .withHeaders("Content-Type" -> "text/json")
              .withQueryString("accessToken" -> token)
              .get().map { response => response.json }
              .andThen {
                case Success(_) => timeRecord.put("Received user info", elapsed())
              }
      } andThen {
        case Failure(NonFatal(ex)) => log.error("Caught error while getting user info", ex)
        case Success(user) => println(s"Received user info = $user")
      }

    /**
     * Search for the query
     */
    val resultF =
      tokenF flatMap { token =>
        val query = Json.obj(
          "queryText" -> "levis men's jeans",
          "pageIndex" -> 1,
          "pageSize"  -> 10
        )

        val elapsed = lapse.Stopwatch.start()
        client.url(BASE_URL + "search/2999388278893")
              .withHeaders("Content-Type" -> "text/json")
              .withQueryString("accessToken" -> token)
              .post(query).map { response => response.json }
              .andThen {
                case Failure(NonFatal(ex)) => log.error("Caught error while searching", ex)
                case Success(result) =>
                  timeRecord.put("Got search result", elapsed())
                  println("\n============== SEARCH RESULTS ================\n")
                  println(Json.prettyPrint(result))
              }
      }

    /**
     * Add items to bucket
     */
    val cudBucketF =
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
                  timeRecord.put("CUD Bucket", elapsed())
                  println("\n================== CUD BUCKET =============\n")
                  println(Json.prettyPrint(result))
              }
              .map(json => (json \ "success").as[Boolean])
      }).flatMap(identity)

      /**
       * Once added Get bucket stores
       */
      val storesF =
        (for {
          token <- tokenF
          success <- cudBucketF if success
        } yield {
          val elapsed = lapse.Stopwatch.start()
          client.url(BASE_URL + "bucket/stores?fields=Name,Address,ItemTypes,CatalogueItemIds")
                .withHeaders("Content-Type" -> "text/json")
                .withQueryString("accessToken" -> token)
                .get().map { response => response.json }
                .andThen {
                  case Failure(NonFatal(ex)) => log.error("Caught error while getting bucket stores")
                  case Success(result) =>
                    timeRecord.put("Got bucket stores", elapsed())
                    println("\n============== BUCKET STORES ===============\n")
                    println(Json.prettyPrint(result))
                }
        }).flatMap(identity)

      /**
       * Create shop plan
       */
      val createShopPlanF =
        (for {
          token  <- tokenF
          stores <- storesF
        } yield {
          val itemIds = (stores \\ "itemId")
          val title = "My shop plan"
          val locations = (stores \\ "address").take(2) // take two as address as the destination

          val destinations = locations.map { addr =>
            Json.obj(
              "destId" -> Json.obj(
                "shopplanId" -> Json.obj(
                  "createdBy" -> Json.obj("uuid" -> -1),
                  "suid" -> -1
                ),
                "dtuid" -> System.currentTimeMillis
              ),
              "address" -> addr
            )
          }

          val cud = Json.obj(
            "meta" -> title,
            "destinations" -> Json.obj(
              "adds" -> destinations
            ),
            "items" -> Json.obj(
              "adds" -> itemIds
            )
          )

          val elapsed = lapse.Stopwatch.start()
          client.url(BASE_URL + "shopplan/create")
                .withHeaders("Content-Type" -> "text/json")
                .withQueryString("accessToken" -> token)
                .post(cud).map { response => response.json }
                .andThen {
                  case Failure(NonFatal(ex)) => log.error("Caught error while creating shopplan")
                  case Success(result) =>
                    timeRecord.put("Create shopplan", elapsed())
                    println("\n============== CREATE SHOPPLAN ===============\n")
                    println(Json.prettyPrint(result))
                }
        }).flatMap(identity)

      /**
       * Get user's shop plans
       */
      val shopplansF =
        (for {
          token <- tokenF
          _     <- createShopPlanF
        } yield {
          val elapsed = lapse.Stopwatch.start()
          client.url(BASE_URL + "shopplan/list/own")
                .withHeaders("Content-Type" -> "text/json")
                .withQueryString("accessToken" -> token)
                .get().map { response => response.json }
                .andThen {
                  case Failure(NonFatal(ex)) => log.error("Caught error while getting own shopplans")
                  case Success(result) =>
                    timeRecord.put("List own shopplans", elapsed())
                    println("\n============== LIST OWN SHOPPLANS ===============\n")
                    println(Json.prettyPrint(result))
                }

        }).flatMap(identity)

      for {
        _ <- tokenF
        _ <- userInfoF
        _ <- resultF
        _ <- cudBucketF
        _ <- storesF
        _ <- createShopPlanF
        _ <- shopplansF
      } {
        println("\n============= API LATENCIES ============\n")
        timeRecord.entrySet foreach { entry =>
          println(entry.getKey + " in " + entry.getValue.toMillis + " ms")
        }
      }

  }

}