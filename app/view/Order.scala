package view

import java.time.format.DateTimeFormatter

import repository.orders.{Order => DBOrder}

object Order {
  val TimeFormat: String = "dd-MM-yyyy HH:mm:ss"

  def fromDB(order: DBOrder): Order = {
    Order(
      id = order.id.toInt,
      status = order.status,
      customer_id = order.customer_id.toInt,
      created_at = order.created_at.format(DateTimeFormatter.ofPattern(TimeFormat)),
      updated_at = order.updated_at.format(DateTimeFormatter.ofPattern(TimeFormat))
    )
  }

  def listFromDB(orders: Seq[DBOrder]): Seq[Order] = {
    orders.map(fromDB)
  }
}

final case class Order(id: Int, status: String, customer_id: Int, created_at: String, updated_at: String) {}
