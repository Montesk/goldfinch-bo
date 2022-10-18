package view

import java.time.format.DateTimeFormatter

import repository.products.{Product => DBProduct}

object Product {
  val TimeFormat: String = "dd-MM-yyyy HH:mm:ss"

  def fromDB(order: DBProduct): Product = {
    Product(
      id = order.id.toInt,
      created_at = order.created_at.format(DateTimeFormatter.ofPattern(TimeFormat)),
      updated_at = order.updated_at.format(DateTimeFormatter.ofPattern(TimeFormat))
    )
  }

  def listFromDB(orders: Seq[DBProduct]): Seq[Product] = {
    orders.map(fromDB)
  }
}

final case class Product(id: Int, created_at: String, updated_at: String) {}
