package repository.products

import java.time.LocalDateTime
import java.util.UUID

import play.api.libs.json._

final case class Product(
    id: Long = 0,
    price: Double,
    name: String,
    stock: Long = 0,
    uuid: Option[UUID] = None,
    description: Option[String],
    disabled: Boolean = false,
    deleted: Boolean = false,
    created_at: Option[LocalDateTime] = None,
    updated_at: Option[LocalDateTime] = None
)

object Product {
  implicit val productFormat: OFormat[Product] = Json.format[Product]
}
