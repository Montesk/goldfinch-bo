package repository.products

import java.time._
import javax.inject.{Inject, Singleton}
import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import play.api.db.slick.DatabaseConfigProvider

case class ProductData(
                        name: String,
                        description: String,
                        price: Int,
                        files: List[String] = List(),
                        titleImage: Option[String] = Option[String](""),
                        deleteFiles: List[String] = List(),
                      )

class Products(tag: Tag) extends Table[Product](tag, "products") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def price = column[Double]("price", O.Default(0.0))

  def name = column[String]("name")

  def stock = column[Long]("stock", O.Default(0))

  def uuid = column[Option[UUID]]("uuid")

  def description = column[Option[String]]("description")

  def disabled = column[Boolean]("disabled", O.Default(false))

  def deleted = column[Boolean]("deleted", O.Default(false))

  def created_at = column[Option[LocalDateTime]]("created_at")

  def updated_at = column[Option[LocalDateTime]]("updated_at")

  def * = (id, price, name, stock, uuid, description, disabled, deleted, created_at, updated_at) <> ((Product.apply _).tupled, Product.unapply)
}

@Singleton
class ProductsRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._

  val products = TableQuery[Products]

  def find(id: Long): Future[Product] = db.run {
    products
      .filter(_.id === id)
      .filter(!_.deleted)
      .result
      .head
  }

  def list(): Future[Seq[Product]] = db.run {
    products
      .filter(!_.deleted)
      .sortBy(_.id.desc)
      .result
  }

  def create(data: ProductData): Future[Any] = db.run {
    DBIO.seq(
      products.map(p => (p.price, p.name, p.description)) += (data.price, data.name, Option[String](data.description))
    )
  }

  def update(id: Long, data: ProductData): Future[Int] = db.run {
    val query = for {c <- products if c.id === id} yield (c.name, c.price, c.description)
    query.update(data.name, data.price, Option[String](data.description))
  }

  def delete(id: Long, soft: Boolean = true): Future[Int] = db.run {
    if (soft) {
      val query = for {p <- products if p.id === id} yield p.deleted
      query.update(true)
    } else {
      products.filter(_.id === id).delete
    }
  }
}