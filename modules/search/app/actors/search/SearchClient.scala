package actors.search

import scala.concurrent.Future
import scala.util.Failure
import scala.util.control.NonFatal

import akka.actor.{Actor, Props, ActorLogging}
import akka.pattern.pipe

import com.goshoplane.common._
import com.goshoplane.creed.service._
import com.goshoplane.cassie.service._
import com.goshoplane.creed.search._

import com.twitter.bijection._, Conversion.asMethod
import com.twitter.bijection.twitter_util.UtilBijections._

import goshoplane.commons.core.protocols._
import goshoplane.commons.catalogue._

import models.search.{SearchResultStore, SearchResult}


/**
 * This Actor acts as a client to Creed search apis
 */
class SearchClient(creed: Creed$FinagleClient, cassie: Cassie$FinagleClient) extends Actor with ActorLogging {

  import context.dispatcher

  def receive = {

    // Search catalogue
    case SearchCatalogue(request) =>
      val creedResultF = creed.searchCatalogue(request)

      // Fetch details in parallel

      // 1. Get Item details
      val itemsF  =
        creedResultF.flatMap { cr =>
          cassie.getCatalogueItems(cr.results.map(_.itemId), CatalogeItemDetailType.Summary)
        } onFailure {
          case NonFatal(ex) =>
            log.error(ex, "Caught error [{}] while getting catalogue items details from cassie for search id = {}.{}",
                          ex.getMessage,
                          request.searchId.userId.uuid,
                          request.searchId.sruid)
        }

      import StoreInfoField._

      val infoFields = Seq(Name, ItemTypes, Address, Avatar, Contacts)

      // 2. Get Store details
      val storesF =
        creedResultF.flatMap { cr =>
          cassie.getStores(cr.results.map(_.itemId.storeId).distinct, infoFields)
        } onFailure {
          case NonFatal(ex) =>
            log.error(ex, "Caught error [{}] while getting stores details from cassie for search id = {}.{}",
                          ex.getMessage,
                          request.searchId.userId.uuid,
                          request.searchId.sruid)
        }


      val resultF =
        for {
          items   <- itemsF
          stores  <- storesF
        } yield {
          val grpdItems = items.groupBy(_.itemId.storeId.stuid)
          // [NOTE] Grouping breaks the ranking of catalogue items
          // and therefore goruping should be handled by the ranker
          // itself [TO DO]
          stores.flatMap { store =>
            grpdItems.get(store.storeId.stuid).map { items =>

              val jsonItems =
                items.flatMap(CatalogueItem.asJsonItem(_))

              SearchResultStore(
                storeId   = store.storeId,
                storeType = store.storeType,
                info      = store.info,
                items     = jsonItems
              )
            }
          }
        }

      // 3. Form SearchResult and send back to sender()
      resultF
        .map(result => SearchResult(searchId = request.searchId, result = result))
        .as[Future[SearchResult]]
        .andThen {
          case Failure(NonFatal(ex)) =>
            log.error(ex, "Caught error [{}] while searching for search id = {}.{}",
                          ex.getMessage,
                          request.searchId.userId.uuid,
                          request.searchId.sruid)
        } pipeTo sender()
  }

}


object SearchClient {
  def props(creed: Creed$FinagleClient, cassie: Cassie$FinagleClient) =
    Props(new SearchClient(creed, cassie))
}

sealed trait SearchClientProtocol
case class SearchCatalogue(request: CatalogueSearchRequest) extends SearchClientProtocol with Replyable[SearchResult]
