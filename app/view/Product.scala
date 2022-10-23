package view

import java.time.format.DateTimeFormatter

import repository.products.{Product => DBProduct}

object Product {
  val TimeFormat: String = "dd-MM-yyyy HH:mm:ss"

  def fromDB(order: DBProduct): Product = {
    Product(
      id = order.id,
      price = order.price.toInt,
      name = order.name,
      stock = order.stock.toInt,
      uuid = order.uuid.get.toString,
      description = order.description,
      disabled = order.disabled,
      created_at = order.created_at.get.format(DateTimeFormatter.ofPattern(TimeFormat)),
      updated_at = order.updated_at.get.format(DateTimeFormatter.ofPattern(TimeFormat))
    )
  }

  def listFromDB(orders: Seq[DBProduct]): Seq[Product] = {
    orders.map(fromDB)
  }

  def create(): Product = {
    Product(
      id = 0,
      price = 0,
      name = "",
      stock = 0,
      uuid = "",
      description = None,
      disabled = false,
      created_at = "",
      updated_at = "",
    )
  }
}

final case class Product(
                          id: Long,
                          price: Int,
                          name: String,
                          stock: Int,
                          uuid: String,
                          description: Option[String],
                          disabled: Boolean,
                          created_at: String,
                          updated_at: String,
                        ) {}
