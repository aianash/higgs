package models

import com.goshoplane.common._
import com.goshoplane.neutrino.feed._

import play.api.libs.json._
import play.api.libs.functional.syntax._


/**
 * Json combinators i.e. Reads, Writes, and Format
 * for Feed related structures
 */
trait FeedJsonCombinators {

  // GSPLocation
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


  // FeedFilter
  protected implicit val feedFilterReads: Reads[FeedFilter] = (
    (__ \ "location") .readNullable[PostalAddress] ~
    (__ \ "page")     .readNullable[Int]
  )(FeedFilter.apply _)


  // PostId
  protected implicit val postIdWrites: Writes[PostId] =
    (__ \ "ptuid").write[Long].contramap[PostId](_.ptuid)


  // StoreId
  protected implicit val storeIdWrites: Writes[StoreId] =
    (__ \ "stuid").write[Long].contramap[StoreId](_.stuid)


  // StoreName
  protected implicit val storeNameWrites: Writes[StoreName] = (
    (__ \ "full").writeNullable[String] ~
    (__ \ "handle").writeNullable[String]
  ) { name: StoreName => (name.full, name.handle) }


  // Offer
  protected implicit val offerWrites: Writes[Offer] = (
    (__ \ "title").write[String] ~
    (__ \ "subtitle").write[String]
  ) { offer: Offer => (offer.title, offer.subtitle) }


  // OfferPost
  protected implicit val offerPostWrites: Writes[OfferPost] = (
    (__ \ "postId")       .write[PostId] ~
    (__ \ "index")        .write[Long] ~
    (__ \ "storeId")      .write[StoreId] ~
    (__ \ "storeName")    .write[StoreName] ~
    (__ \ "storeAddress") .write[PostalAddress] ~
    (__ \ "offer")        .write[Offer]
  ) { post: OfferPost =>
      import post._
      (postId, index, storeId, storeName, storeAddress, offer)
  }


  // Seq[OfferPost]
  protected implicit val offerPostsWrites: Writes[Seq[OfferPost]] =
    Writes(posts => JsArray(posts.map(Json.toJson(_))))


  // PosterImage
  protected implicit val posterImageWrites: Writes[PosterImage] =
    (__ \ "link").write[String].contramap[PosterImage](_.link)


  // PosterAd
  protected implicit val posterAdWrites: Writes[PosterAd] = (
    (__ \ "paduid") .write[Long] ~
    (__ \ "image")  .write[PosterImage]
  ) { ad: PosterAd => (ad.paduid, ad.image) }


  // PosterAdPost
  protected implicit val posterAdPostWrites: Writes[PosterAdPost] = (
    (__ \ "postId") .write[PostId] ~
    (__ \ "index")  .write[Long] ~
    (__ \ "poster") .write[PosterAd]
  ) { post: PosterAdPost =>
      import post._
      (postId, index, poster)
  }


  // PosterAdPost
  protected implicit val posterAdPostsWrites: Writes[Seq[PosterAdPost]] =
    Writes(posts => JsArray(posts.map(Json.toJson(_))))


  // Feed
  protected implicit val feedWrites: Writes[Feed] = (
    (__ \ "offerPosts") .write[Seq[OfferPost]] ~
    (__ \ "PosterAdPosts").write[Seq[PosterAdPost]] ~
    (__ \ "page").write[Int]
  ) { feed: Feed => (feed.offerPosts, feed.posterAdPosts, feed.page) }

}