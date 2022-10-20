package view

import java.time.format.DateTimeFormatter

import repository.products.{Product => DBProduct}

object Product {
  val TimeFormat: String = "dd-MM-yyyy HH:mm:ss"

  def fromDB(order: DBProduct): Product = {
    Product(
      id = order.id,
      price = order.price.toString,
      name = order.name,
      stock = order.stock.toInt,
      uuid = order.uuid.toString,
      description = order.description,
      disabled = order.disabled,
      created_at = order.created_at.format(DateTimeFormatter.ofPattern(TimeFormat)),
      updated_at = order.updated_at.format(DateTimeFormatter.ofPattern(TimeFormat))
    )
  }

  def listFromDB(orders: Seq[DBProduct]): Seq[Product] = {
    orders.map(fromDB)
  }
}

final case class Product(
                          id: Long,
                          price: String,
                          name: String,
                          stock: Int,
                          uuid: String,
                          description: Option[String],
                          disabled: Boolean,
                          created_at: String,
                          updated_at: String,
                        ) {}
