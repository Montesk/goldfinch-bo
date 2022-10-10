package repository.product

import play.api.libs.json._

import java.time.LocalDateTime

case class Product(
                    id: Long,
                    price: Double,
                    name: String,
                    stock: Long,
                    created_at: LocalDateTime,
                    updated_at: LocalDateTime,
                  )

object Product {
  implicit val productFormat: OFormat[Product] = Json.format[Product]
}