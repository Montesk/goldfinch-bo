package repository.products

import java.time.LocalDateTime
import java.util.UUID

import play.api.libs.json._


case class Product(
                    id: Long,
                    price: Double,
                    name: String,
                    stock: Long,
                    uuid: UUID,
                    description: Option[String],
                    disabled: Boolean,
                    created_at: LocalDateTime,
                    updated_at: LocalDateTime,
                  )

object Product {
  implicit val productFormat: OFormat[Product] = Json.format[Product]
}