package repository.deliveries

import java.time.LocalDateTime

case class Delivery(
    id: Long = 0,
    product_id: Long = 0,
    quantity: Int = 0,
    created_at: Option[LocalDateTime] = None,
    updated_at: Option[LocalDateTime] = None
)
