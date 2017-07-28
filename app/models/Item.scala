package models

import play.api.data.Form
import play.api.data.Forms._
import scala.collection.mutable.ArrayBuffer

case class Item(name: String, description: String, maker: String, warranty: String,
              price: Int, discount: Int, seller: String, picture: String)

case class ItemName(name: String)

object Item{
  val createItemForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "maker" -> nonEmptyText,
      "warranty" -> nonEmptyText,
      "price" -> number,
      "discount" -> number,
      "seller" -> nonEmptyText,
      "picture" -> nonEmptyText
    )(Item.apply)(Item.unapply)
  )

  val createNameForm = Form(
    mapping(
      "name" -> nonEmptyText
    )(ItemName.apply)(ItemName.unapply)
  )

  val items = ArrayBuffer(
    Item("Item1", "some description", "maker", "2 years", 225, 25, "seller", "picture location"),
    Item("Item2", "some description", "maker", "3 years", 125, 12, "seller", "picture location"),
    Item("Item3", "some description", "maker", "1 years", 50, 5, "seller", "picture location")
  )
}

object JsonFormats {
  import play.api.libs.json.Json
  implicit val itemFormat = Json.format[Item]
}