package repository.orders

import java.time.LocalDateTime

import play.api.libs.json._

case class Order(
    id: Long,
    status: String,
    customer_id: Long,
    created_at: LocalDateTime,
    updated_at: LocalDateTime
)

object Order {
  implicit val personFormat: OFormat[Order] = Json.format[Order]
}
