package view

import java.time.format.DateTimeFormatter
import java.util.UUID

import repository.products.{Product => DBProduct}
import services.assets.{Manager, Assets => AService}
import controllers.routes

final case class Product(
                          id: Int,
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

object Product extends DynamicLinker {
  val TimeFormat: String = "dd-MM-yyyy HH:mm:ss"
  val MainImageFileName: String = "main"

  private val assetsManager: Manager = new AService

  type B = Int

  override def uriTo[A <: B](identifier: A): String = {
    ProductPath(identifier.toLong)
  }

  def fromDB(product: DBProduct): Product = {
    Product(
      id = product.id.toInt,
      price = product.price.toInt,
      name = product.name,
      stock = product.stock.toInt,
      uuid = product.uuid.get.toString,
      description = product.description,
      disabled = product.disabled,
      created_at = product.created_at.get.format(DateTimeFormatter.ofPattern(TimeFormat)),
      updated_at = product.updated_at.get.format(DateTimeFormatter.ofPattern(TimeFormat)),
      files = assetsManager.getGroupAssets(product.uuid.get.toString, List(MainImageFileName)).map(file => buildAssetPath(product.uuid.get, file)),
      titleImage = buildAssetPath(product.uuid.get, assetsManager.getFileByName(product.uuid.get.toString, MainImageFileName)),
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

  private def buildAssetPath(group: UUID, fileName: String): String = {
    routes.Assets.versioned(s"${assetsManager.getAssetsPath}/${group.toString}/${fileName}").url
  }
}
