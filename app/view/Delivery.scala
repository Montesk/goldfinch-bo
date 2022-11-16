package view

import java.time.format.DateTimeFormatter

import repository.deliveries.{Delivery => DBDelivery}
import repository.products.{Product => DBProduct}

final case class Delivery(
                           id: Int,
                           product_id: Int,
                           product: Product,
                           quantity: Int,
                           created_at: String,
                           updated_at: String,
                         ) {}

object Delivery extends DynamicLinker {
  val TimeFormat: String = "dd-MM-yyyy HH:mm:ss"

  type B = Int

  override def uriTo[A <: B](identifier: A): String = {
    deliveryPath(identifier.toLong)
  }

  def fromDB(delivery: DBDelivery, product: DBProduct): Delivery = {
    Delivery(
      id = delivery.id.toInt,
      product_id = delivery.product_id.toInt,
      quantity = delivery.quantity,
      product = Product.fromDB(product),
      created_at = delivery.created_at.get.format(DateTimeFormatter.ofPattern(TimeFormat)),
      updated_at = delivery.updated_at.get.format(DateTimeFormatter.ofPattern(TimeFormat)),
    )
  }

  def listFromDB(orders: Vector[(DBDelivery, DBProduct)]): Vector[Delivery] = {
    orders.map(tpl => {
      fromDB(tpl._1, tpl._2)
    })
  }

  def newDelivery(): Delivery = {
    Delivery(
      id = 0,
      product_id = 0,
      product = null,
      quantity = 0,
      created_at = "",
      updated_at = "",
    )
  }
}
