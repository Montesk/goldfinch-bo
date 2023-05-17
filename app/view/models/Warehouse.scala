package view.models

import java.time.format.DateTimeFormatter

import repository.warehouses.{Warehouse => DBWarehouse}

final case class Warehouse(
    id: Int,
    name: String,
    description: String,
    deleted: Boolean,
    disabled: Boolean,
    created_at: String,
    updated_at: String,
) {}

object Warehouse {
  val TimeFormat: String = "dd-MM-yyyy HH:mm:ss"

  def fromDB(warehouse: DBWarehouse): Warehouse = {
    Warehouse(
      id = warehouse.id.toInt,
      name = warehouse.name,
      description = warehouse.description.getOrElse(""),
      deleted = warehouse.deleted,
      disabled = warehouse.disabled,
      created_at = warehouse.created_at.get.format(DateTimeFormatter.ofPattern(TimeFormat)),
      updated_at = warehouse.updated_at.get.format(DateTimeFormatter.ofPattern(TimeFormat)),
    )
  }

  def listFromDB(warehouses: Vector[DBWarehouse]): Vector[Warehouse] = {
    warehouses.map(fromDB)
  }
}
