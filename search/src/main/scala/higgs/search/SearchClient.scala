package higgs
package search

import scala.concurrent.duration._
import scala.util.Random

import play.api.libs.json._

import akka.actor.{Props, Actor, ActorLogging, Cancellable}

import core.capsule._


class SearchClient(capsule: SearchCapsule) extends Actor with ActorLogging {

  import context.dispatcher

  var styleSugg: Cancellable = _

  def receive = {
    case GetSearchResultFor(searchId) =>
      context.system.scheduler.scheduleOnce(2000 milliseconds) {
        capsule.sendResponse(SearchResult(
          searchId = searchId,
          result =
            List(item1, item2, item3, item1, item2, item3,
                item1, item2, item3, item1, item2, item3,
                item1, item2, item3, item1, item2, item3,
                item1, item2, item3, item1, item2, item3)
        ), "/search/reasult")
      }


    case UpdateQueryFor(searchId) =>
      if(styleSugg == null || Random.nextBoolean()) {
        styleSugg.cancel
        styleSugg =
          context.system.scheduler.scheduleOnce(1000 milliseconds) {
            capsule.sendMessage(Message(searchId.userId,
              "/query/suggestions/styles",
              Json.obj (
                "sruid" -> searchId.sruid.toString,
                "styles" -> Json.arr(
                  "Tees Top",
                  "Bodysuit Top",
                  "Crop Top",
                  "Tube Top",
                  "Peplum Top",
                  "Cowl Top",
                  "Spaghetti Top"
                )
              )
            ))
          }
      }
  }

  val item1 = Json.obj(
    "cuid" -> "199282919",
    "title" -> "Blue Tiara Top",
    "price" -> 549f,
    "sizes" -> Json.arr("3XS", "2XS", "XS"),
    "colors" -> Json.arr("Blue", "Black"),
    "brand" -> Json.obj(
      "id" -> "192881",
      "name" -> "StalkBuyLove"
    ),
    "descr" -> """
Women's fashion top made with stretchable knit cotton spandex
Color block design
Spaghetti shoulder straps
Our European styles are designed in-house by our highly qualified designers.
We use high-quality fabrics and trendiest colours to ensure that you look fabulous.
We promise that you will look fabulous in this style. If not, you can return the product within 7 days no-questions-asked!
We do not offer high discounts in order to sustain high quality standards and designs that we provide.
    """,
    "stylingTips" -> "Va Va Voom for this sexy top! This one is our personal favourite cause it has everything to complete a sexy look. A funky neckline- check!, spagetti straps - check!, figure hugging and soft material - check! Pair this top us with a colouful pair of skinny jeans; the yellow and blue combination is trending a lot lately so maybe you can give that a try. Dont wear a neck piece with this top. Let the focus be on the gorgeous neckline.",
    "images" -> Json.arr(
      "http://img.stalkbuylove.com/media/catalog/product/cache/1/image/870x1100/9df78eab33525d08d6e5fb8d27136e95/i/n/in1514mtotopblu-131-option.jpg",
      "http://img.stalkbuylove.com/media/catalog/product/cache/1/image/870x1100/9df78eab33525d08d6e5fb8d27136e95/i/n/in1514mtotopblu-131-ghost.jpg",
      "http://img.stalkbuylove.com/media/catalog/product/cache/1/image/870x1100/9df78eab33525d08d6e5fb8d27136e95/i/n/in1514mtotopblu-131-front.jpg"),
    "primaryImage" -> "http://img.stalkbuylove.com/media/catalog/product/cache/1/image/870x1100/9df78eab33525d08d6e5fb8d27136e95/i/n/in1514mtotopblu-131-front.jpg",
    "stores" -> Json.arr(
        Json.obj(
          "storeId" -> "12",
          "name" -> "StalkBuyLove",
          "url" -> "http://www.stalkbuylove.com/blue-tiara-top-83475-SBLPR.html")
    ),
    "styles" -> Json.arr("Spaghetti Top"),
    "itemTypeGroup" -> "Tops",
    "groups" -> Json.arr("Clothing", "Womens Clothing", "Womens Tops")
  )

  val item2 = Json.obj(
    "cuid" -> "1992829192",
    "title" -> "Peachy Pop Up Top",
    "price" -> 549f,
    "sizes" -> Json.arr("3XS", "2XS", "XS"),
    "colors" -> Json.arr("Peach", "Mint", "Blue", "Green"),
    "brand" -> Json.obj(
      "id" -> "192881",
      "name" -> "StalkBuyLove"
    ),
    "descr" -> """
Women's fashion top made with stretchable knit cotton spandex
Color block design
Spaghetti shoulder straps
Our European styles are designed in-house by our highly qualified designers.
We use high-quality fabrics and trendiest colours to ensure that you look fabulous.
We promise that you will look fabulous in this style. If not, you can return the product within 7 days no-questions-asked!
We do not offer high discounts in order to sustain high quality standards and designs that we provide.
    """,
    "stylingTips" -> "This Peachy Pop Up top is made with polyester crepe, and has a colour block design. The elasticized sleeve openings and tie up straps at the back are its defining features. Wear this with your favourite pair of jeggings or jeans to finish this look!",
    "images" -> Json.arr(
      "http://img.stalkbuylove.com/media/catalog/product/cache/1/image/870x1100/9df78eab33525d08d6e5fb8d27136e95/i/n/in1432mtotoppnk-345-front-sbl_1.jpg",
      "http://img.stalkbuylove.com/media/catalog/product/cache/1/image/870x1100/9df78eab33525d08d6e5fb8d27136e95/i/n/in1432mtotoppnk-345-back-sbl_1.jpg",
      "http://img.stalkbuylove.com/media/catalog/product/cache/1/image/870x1100/9df78eab33525d08d6e5fb8d27136e95/i/n/in1432mtotoppnk-345-option-sbl_1.jpg"),
    "primaryImage" -> "http://img.stalkbuylove.com/media/catalog/product/cache/1/image/870x1100/9df78eab33525d08d6e5fb8d27136e95/i/n/in1432mtotoppnk-345-front-sbl_1.jpg",
    "stores" -> Json.arr(
        Json.obj(
          "storeId" -> "12",
          "name" -> "StalkBuyLove",
          "url" -> "http://www.stalkbuylove.com/peachy-pop-up-top-78135-SBLPR.html")
    ),
    "styles" -> Json.arr("Crop Top"),
    "itemTypeGroup" -> "Tops",
    "groups" -> Json.arr("Clothing", "Womens Clothing", "Womens Tops")
  )

  val item3 = Json.obj(
    "cuid" -> "1992821919",
    "title" -> "Untie & Reveal Top",
    "price" -> 549f,
    "sizes" -> Json.arr("3XS", "2XS", "XS"),
    "colors" -> Json.arr("Pink"),
    "brand" -> Json.obj(
      "id" -> "192881",
      "name" -> "StalkBuyLove"
    ),
    "descr" -> """
Women's fashion top made with stretchable knit cotton spandex
Color block design
Spaghetti shoulder straps
Our European styles are designed in-house by our highly qualified designers.
We use high-quality fabrics and trendiest colours to ensure that you look fabulous.
We promise that you will look fabulous in this style. If not, you can return the product within 7 days no-questions-asked!
We do not offer high discounts in order to sustain high quality standards and designs that we provide.
    """,
    "stylingTips" -> "Va Va Voom for this sexy top! This one is our personal favourite cause it has everything to complete a sexy look. A funky neckline- check!, spagetti straps - check!, figure hugging and soft material - check! Pair this top us with a colouful pair of skinny jeans; the yellow and blue combination is trending a lot lately so maybe you can give that a try. Dont wear a neck piece with this top. Let the focus be on the gorgeous neckline.",
    "images" -> Json.arr(
      "http://img.stalkbuylove.com/media/catalog/product/cache/1/image/870x1100/9df78eab33525d08d6e5fb8d27136e95/i/n/in1504mtotoppnk-108-front.jpg",
      "http://img.stalkbuylove.com/media/catalog/product/cache/1/image/870x1100/9df78eab33525d08d6e5fb8d27136e95/i/n/in1504mtotoppnk-108-back.jpg",
      "http://img.stalkbuylove.com/media/catalog/product/cache/1/image/870x1100/9df78eab33525d08d6e5fb8d27136e95/i/n/in1504mtotoppnk-108-option.jpg"),
    "primaryImage" -> "http://img.stalkbuylove.com/media/catalog/product/cache/1/image/870x1100/9df78eab33525d08d6e5fb8d27136e95/i/n/in1504mtotoppnk-108-front.jpg",
    "stores" -> Json.arr(
        Json.obj(
          "storeId" -> "12",
          "name" -> "StalkBuyLove",
          "url" -> "http://www.stalkbuylove.com/untie-reveal-top-73263-SBLPR.html")
    ),
    "styles" -> Json.arr("Crop Top"),
    "itemTypeGroup" -> "Tops",
    "groups" -> Json.arr("Clothing", "Womens Clothing", "Womens Tops")
  )

}


object SearchClient {
  def props(capsule: SearchCapsule) = Props(classOf[SearchClient], capsule)
}