package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.goshoplane.neutrino.service._
import com.goshoplane.neutrino.shopplan._
import com.goshoplane.common._


/**
 * Json combinators i.e. Reads, Writes, and Format
 * for ShopPlan related structures
 */
trait ShopPlanJsonCombinators {
  import Reads._

  // UserId
  protected val userIdReads: Reads[UserId] =
    (__ \ "uuid").read[Long].map(UserId(_))

  protected val userIdWrites: Writes[UserId] =
    (__ \ "uuid").write[Long].contramap[UserId](_.uuid)

  protected implicit val userIdFormat: Format[UserId] =
    Format(userIdReads, userIdWrites)


  // ShopPlanId
  protected val shopplanIdReads: Reads[ShopPlanId] = (
    (__ \ "createdBy").read[UserId] ~
    (__ \ "suid")     .read[Long]
  )(ShopPlanId.apply _)

  protected val shopplanIdWrites: Writes[ShopPlanId] = (
    (__ \ "createdBy").write[UserId] ~
    (__ \ "suid")     .write[Long]
  ) { id: ShopPlanId => (id.createdBy, id.suid) }

  protected implicit val shopplanIdFormat: Format[ShopPlanId] =
    Format(shopplanIdReads, shopplanIdWrites)


  // StoreId
  protected val storeIdReads: Reads[StoreId] =
    (__ \ "stuid").read[Long].map(StoreId(_))

  protected val storeIdWrites: Writes[StoreId] =
    (__ \ "stuid").write[Long].contramap[StoreId](_.stuid)

  protected implicit val storeIdFormat: Format[StoreId] =
    Format(storeIdReads, storeIdWrites)


  // DestinationId
  protected val destinationIdReads: Reads[DestinationId] = (
    (__ \ "shopplanId").read[ShopPlanId] ~
    (__ \ "dtuid")     .read[Long]
  )(DestinationId.apply _)

  protected val destinationIdWrites: Writes[DestinationId] = (
    (__ \ "shopplanId").write[ShopPlanId] ~
    (__ \ "dtuid")     .write[Long]
  ) { id: DestinationId => (id.shopplanId, id.dtuid) }

  protected implicit val destinationIdForma: Format[DestinationId] =
    Format(destinationIdReads, destinationIdWrites)


  // StoreType
  protected implicit val storeTypeWrites: Writes[StoreType] =
    Writes(st => JsString(st.name))


  // StoreName
  protected implicit val storeNameWrites: Writes[StoreName] = (
    (__ \ "full")  .writeNullable[String] ~
    (__ \ "handle").writeNullable[String]
  ) { name: StoreName => (name.full, name.handle) }


  // ItemType
  protected implicit val itemTypeWrites: Writes[ItemType] =
    Writes(it => JsString(it.name))


  // GPSLocation
  protected val gpsLocationReads: Reads[GPSLocation] = (
    (__ \ "lat").read[Double] ~
    (__ \ "lng").read[Double]
  )(GPSLocation.apply _)

  protected val gpsLocationWrites: Writes[GPSLocation] = (
    (__ \ "lat").write[Double] ~
    (__ \ "lng").write[Double]
  ) { gps: GPSLocation => (gps.lat, gps.lng) }

  protected implicit val gpsLocationFormat: Format[GPSLocation] =
    Format(gpsLocationReads, gpsLocationWrites)


  // PostalAddress
  protected val postalAddressReads: Reads[PostalAddress] = (
    (__ \ "gpsLoc") .readNullable[GPSLocation] ~
    (__ \ "title")  .readNullable[String] ~
    (__ \ "short")  .readNullable[String] ~
    (__ \ "full")   .readNullable[String] ~
    (__ \ "pincode").readNullable[String] ~
    (__ \ "country").readNullable[String] ~
    (__ \ "city")   .readNullable[String]
  )(PostalAddress.apply _)

  protected val postalAddressWrites: Writes[PostalAddress] = (
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

  protected implicit val postalAddressFormat: Format[PostalAddress] =
    Format(postalAddressReads, postalAddressWrites)


  // StoreAvatar
  protected implicit val storeAvatarWrites: Writes[StoreAvatar] = (
    (__ \ "small") .writeNullable[String] ~
    (__ \ "medium").writeNullable[String] ~
    (__ \ "large") .writeNullable[String]
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


  // CatalogueItemId
  val catalogueItemIdReads: Reads[CatalogueItemId] = (
    (__ \ "storeId").read[StoreId] ~
    (__ \ "ctuid")  .read[Long]
  )(CatalogueItemId.apply _)

  val catalogueItemIdWrites: Writes[CatalogueItemId] = (
    (__ \ "storeId").write[StoreId] ~
    (__ \ "ctuid")  .write[Long]
  ) { cid: CatalogueItemId => (cid.storeId, cid.cuid)}

  protected implicit val catalogueItemIdFormat: Format[CatalogueItemId] =
    Format(catalogueItemIdReads, catalogueItemIdWrites)


  // JsonCatalogueItem
  protected implicit val jsonCatalogueItemWrites: Writes[JsonCatalogueItem] =
    Writes(item => Json.parse(item.json))


  // ShopPlanStore
  protected implicit val shopPlanStoreWrites: Writes[ShopPlanStore] = (
    (__ \ "storeId")       .write[StoreId] ~
    (__ \ "destId")        .write[DestinationId] ~
    (__ \ "storeType")     .write[StoreType] ~
    (__ \ "info")          .write[StoreInfo] ~
    (__ \ "catalogueItems").writeNullable[Seq[JsonCatalogueItem]] ~
    (__ \ "itemIds")       .writeNullable[Seq[CatalogueItemId]]
  ) { store: ShopPlanStore =>
      import store._
      (storeId, destId, storeType, info, catalogueItems, itemIds)
  }


  // Destination
  protected val destinationWrites: Writes[Destination] = (
    (__ \ "destId")  .write[DestinationId] ~
    (__ \ "address") .write[PostalAddress] ~
    (__ \ "numShops").writeNullable[Int]
  ) { destination: Destination =>
      import destination._
      (destId, address, numShops)
  }

  protected val destinationReads: Reads[Destination] = (
    (__ \ "destId")  .read[DestinationId] ~
    (__ \ "address") .read[PostalAddress] ~
    (__ \ "numShops").readNullable[Int]
  )(Destination.apply _)

  protected implicit val destinationFormat: Format[Destination] =
    Format(destinationReads, destinationWrites)


  // UserName
  protected implicit val usernameWrites: Writes[UserName] = (
    (__ \ "first") .writeNullable[String] ~
    (__ \ "last")  .writeNullable[String] ~
    (__ \ "handle").writeNullable[String]
  ) { name: UserName => (name.first, name.last, name.handle) }


  // UserAvatar
  protected implicit val userAvatarWrites: Writes[UserAvatar] = (
    (__ \ "small") .writeNullable[String] ~
    (__ \ "medium").writeNullable[String] ~
    (__ \ "large") .writeNullable[String]
  ) { avatar: UserAvatar =>
      import avatar._
      (small, medium, large)
  }


  // InviteStatus
  protected implicit val inviteStatusWrites: Writes[InviteStatus] =
    Writes(status => JsString(status.name))


  // Invite
  protected implicit val inviteWrites: Writes[Invite] = (
    (__ \ "friendId")     .write[UserId] ~
    (__ \ "shopplanId")   .write[ShopPlanId] ~
    (__ \ "name")         .writeNullable[UserName] ~
    (__ \ "avatar")       .writeNullable[UserAvatar] ~
    (__ \ "inviteStatus") .writeNullable[InviteStatus]
  ) { invite: Invite =>
      import invite._
      (friendId, shopplanId, name, avatar, inviteStatus)
  }


  // ShopPlan
  protected implicit val shopPlanWrites: Writes[ShopPlan] = (
    (__ \ "shopplanId")   .write[ShopPlanId] ~
    (__ \ "title")        .writeNullable[String] ~
    (__ \ "stores")       .writeNullable[Seq[ShopPlanStore]] ~
    (__ \ "destinations") .writeNullable[Seq[Destination]] ~
    (__ \ "invites")      .writeNullable[Seq[Invite]] ~
    (__ \ "isInvitation") .write[Boolean]
  ) { plan: ShopPlan =>
      import plan._
      (shopplanId, title, stores, destinations, invites, isInvitation)
  }


  /**
   * [NOTE] Reads for CUDShopPlan and adjacent cud's is
   * implemented only specific to create new shop plan
   * operation. For modifying existing, these reads
   * will be updated
   */
  // CUDShopPlanMeta
  protected implicit val cudShopPlanMetaReads: Reads[CUDShopPlanMeta] =
    (__ \ "title").readNullable[String].map(CUDShopPlanMeta(_))


  // CUDDestinations
  protected implicit val cudDestinationsReads: Reads[CUDDestinations] = (
   (__ \ "adds")    .readNullable[Seq[Destination]] ~
   (__ \ "updates") .readNullable[Seq[Destination]] ~
   (__ \ "removals").readNullable[Seq[DestinationId]]
  )(CUDDestinations.apply _)


  // CUDInvites
  protected implicit val cudInvitesReads: Reads[CUDInvites] = (
    (__ \ "adds")     .readNullable[Seq[UserId]] ~
    (__ \ "removals") .readNullable[Seq[UserId]]
  )(CUDInvites.apply _)


  // CUDShopPlanItems
  protected implicit val cudShopPlanItemsReads: Reads[CUDShopPlanItems] = (
    (__ \ "adds")     .readNullable[Seq[CatalogueItemId]] ~
    (__ \ "removals") .readNullable[Seq[CatalogueItemId]]
  )(CUDShopPlanItems.apply _)


  // CUDShopPlan
  protected implicit val cudShopPlanReads: Reads[CUDShopPlan] = (
    (__ \ "meta")         .readNullable[CUDShopPlanMeta] ~
    (__ \ "destinations") .readNullable[CUDDestinations] ~
    (__ \ "invites")      .readNullable[CUDInvites] ~
    (__ \ "items")        .readNullable[CUDShopPlanItems]
  )(CUDShopPlan.apply _)

}