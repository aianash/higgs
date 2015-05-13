package models

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
trait SearchJsonCombinators extends ShopPlanJsonCombinators {

  protected implicit val catalogueSearchIdWrites: Writes[CatalogueSearchId] = (
    (__ \ "userId").write[UserId] ~
    (__ \ "sruid") .write[Long]
  ) { id: CatalogueSearchId => (id.userId, id.sruid) }

  protected implicit val searchResultStoreWrites = Json.writes[SearchResultStore]
  protected implicit val searchResultWrites = Json.writes[SearchResult]
  protected implicit val searchRequestReads = Json.reads[SearchRequest]

}