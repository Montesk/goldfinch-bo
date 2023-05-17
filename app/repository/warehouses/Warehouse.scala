package repository.warehouses

import java.time.LocalDateTime

case class Warehouse(
    id: Long = 0,
    name: String,
    description: Option[String],
    deleted: Boolean = false,
    disabled: Boolean = false,
    created_at: Option[LocalDateTime] = None,
    updated_at: Option[LocalDateTime] = None,
)

object Warehouse {}
