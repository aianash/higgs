package models.bucket

import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.goshoplane.neutrino.service._
import com.goshoplane.neutrino.shopplan._
import com.goshoplane.common._


/**
 * Json combinators i.e. Reads, Writes, and Format
 * for Bucket related structures
 */
trait BucketJsonCombinators {
  import Reads._

  // BucketStoreField
  protected implicit val bucketStoreFieldReads: Reads[BucketStoreField] =
    Reads[BucketStoreField] { json =>
      StringReads.reads(json).flatMap { value =>
        BucketStoreField.valueOf(value) match {
          case Some(field) => JsSuccess(field)
          case None        => JsError("erroe.expected.bucketStoreField")
        }
      }
    }


  // StoreId
  protected val storeIdReads: Reads[StoreId] =
    (__ \ "stuid").read[String].map(id => StoreId(id.toLong))

  protected val storeIdWrites: Writes[StoreId] =
    (__ \ "stuid").write[String].contramap[StoreId](_.stuid.toString)

  protected implicit val storeIdFormat: Format[StoreId] =
    Format(storeIdReads, storeIdWrites)


  // CatalogueItemId
  val catalogueItemIdReads: Reads[CatalogueItemId] = (
    (__ \ "storeId").read[StoreId] ~
    (__ \ "cuid")   .read[String].map(_.toLong)
  )(CatalogueItemId.apply _)

  val catalogueItemIdWrites: Writes[CatalogueItemId] = (
    (__ \ "storeId").write[StoreId] ~
    (__ \ "cuid")   .write[String]
  ) { cid: CatalogueItemId => (cid.storeId, cid.cuid.toString) }

  protected implicit val catalogueItemIdFormat: Format[CatalogueItemId] =
    Format(catalogueItemIdReads, catalogueItemIdWrites)


  // StoreType
  protected implicit val storeTypeWrites: Writes[StoreType] =
    Writes(st => JsString(st.name))


  // StoreName
  protected implicit val storeNameWrites: Writes[StoreName] = (
    (__ \ "full").writeNullable[String] ~
    (__ \ "handle").writeNullable[String]
  ) { name: StoreName => (name.full, name.handle) }


  // ItemType
  protected implicit val itemTypeWrites: Writes[ItemType] =
    Writes(it => JsString(it.name))


  // GPSLocation
  protected implicit val gpsLocationWrites: Writes[GPSLocation] = (
    (__ \ "lat").write[Double] ~
    (__ \ "lng").write[Double]
  ) { gps: GPSLocation => (gps.lat, gps.lng) }


  // PostalAddress
  protected implicit val postalAddressWrites: Writes[PostalAddress] = (
    (__ \ "gpsLoc") .writeNullable[GPSLocation] ~
    (__ \ "title")  .writeNullable[String] ~
    (__ \ "short")  .writeNullable[String] ~
    (__ \ "full")   .writeNullable[String] ~
    (__ \ "pincode").writeNullable[String] ~
    (__ \ "country").writeNullable[String] ~
    (__ \ "city")   .writeNullable[String]
  ) { addr: PostalAddress =>
      import addr._
      (gpsLoc, title, short, full, pincode, country, city)
  }


  // StoreAvatar
  protected implicit val storeAvatarWrites: Writes[StoreAvatar] = (
    (__ \ "small")  .writeNullable[String] ~
    (__ \ "medium") .writeNullable[String] ~
    (__ \ "large")  .writeNullable[String]
  ) { avatar: StoreAvatar =>
      import avatar._
      (small, medium, large)
  }


  // PhoneContact
  protected implicit val phoneContactWrites: Writes[PhoneContact] =
    Writes(pc => JsArray(pc.numbers.map(Json.toJson(_))))


  // StoreInfo
  protected implicit val storeInfoWrites: Writes[StoreInfo] = (
    (__ \ "name")     .writeNullable[StoreName] ~
    (__ \ "itemTypes").writeNullable[Seq[ItemType]] ~
    (__ \ "address")  .writeNullable[PostalAddress] ~
    (__ \ "avatar")   .writeNullable[StoreAvatar] ~
    (__ \ "email")    .writeNullable[String] ~
    (__ \ "phone")    .writeNullable[PhoneContact]
  ) { info: StoreInfo =>
      import info._
      (name, itemTypes, address, avatar, email, phone)
  }


  // JsonCatalogueItem
  protected implicit val jsonCatalogueItemWrites: Writes[JsonCatalogueItem] =
    Writes(item =>
      if(item.json.isEmpty) Json.obj("itemId" -> Json.toJson(item.itemId))
      else  Json.parse(item.json))


  // BucketStore
  protected implicit val bucketStoreWrites: Writes[BucketStore] = (
    (__ \ "storeId") .write[StoreId] ~
    (__ \ "storeType").write[StoreType] ~
    (__ \ "info").write[StoreInfo] ~
    (__ \ "catalogueItems").writeNullable[Seq[JsonCatalogueItem]]
  ) { store: BucketStore =>
      import store._
      (storeId, storeType, info, catalogueItems)
  }


  // CUDBucket
  protected implicit val cudBucketReads: Reads[CUDBucket] =
    (__ \ "adds").readNullable[Seq[CatalogueItemId]].map(CUDBucket(_))

}