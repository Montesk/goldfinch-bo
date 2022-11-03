package view

import java.time.format.DateTimeFormatter

import repository.products.{Product => DBProduct}
import services.assets.{Manager, Assets}

object Product {
  val TimeFormat: String = "dd-MM-yyyy HH:mm:ss"

  private val assetsManager: Manager = new Assets

  def fromDB(product: DBProduct): Product = {
    Product(
      id = product.id,
      price = product.price.toInt,
      name = product.name,
      stock = product.stock.toInt,
      uuid = product.uuid.get.toString,
      description = product.description,
      disabled = product.disabled,
      created_at = product.created_at.get.format(DateTimeFormatter.ofPattern(TimeFormat)),
      updated_at = product.updated_at.get.format(DateTimeFormatter.ofPattern(TimeFormat)),
      files = assetsManager.GetGroupAssets(product.uuid.get.toString, List("main")),
      titleImage = assetsManager.GetFileByName(product.uuid.get.toString, "main"),
    )
  }

  def listFromDB(orders: Seq[DBProduct]): Seq[Product] = {
    orders.map(fromDB)
  }

  def newProduct(): Product = {
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
                          files: List[String] = List(),
                          titleImage: String = ""
                        ) {}
