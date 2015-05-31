package models.search

import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.goshoplane.common._
import com.goshoplane.creed.search._


case class SearchResultStore(storeId: StoreId, storeType: StoreType, info: StoreInfo, items: Seq[JsonCatalogueItem])
case class SearchResult(searchId: CatalogueSearchId, result: Seq[SearchResultStore])
case class SearchRequest(queryText: String, pageIndex: Int, pageSize: Int) // [TO DO] support more query params


/**
 * Json combinators i.e. Reads, Writes, and Format
 * for Search related structures
 */
trait SearchJsonCombinators extends models.shopplan.ShopPlanJsonCombinators {

  protected implicit val catalogueSearchIdWrites: Writes[CatalogueSearchId] = (
    (__ \ "userId").write[UserId] ~
    (__ \ "sruid") .write[String]
  ) { id: CatalogueSearchId => (id.userId, id.sruid.toString) }

  protected implicit val searchResultStoreWrites = Json.writes[SearchResultStore]
  protected implicit val searchResultWrites = Json.writes[SearchResult]
  protected implicit val searchRequestReads = Json.reads[SearchRequest]

}