package repository.products

import java.time._
import javax.inject.{Inject, Singleton}
import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider

case class productEditData(name: String, description: String, price: Int)

@Singleton
class ProductsRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class ProductsTable(tag: Tag) extends Table[Product](tag, "products") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def price = column[Double]("price", O.Default(0.0))

    def name = column[String]("name")

    def stock = column[Long]("stock", O.Default(0))

    def uuid = column[Option[UUID]]("uuid")

    def description = column[Option[String]]("description")

    def disabled = column[Boolean]("disabled", O.Default(false))

    def created_at = column[Option[LocalDateTime]]("created_at")

    def updated_at = column[Option[LocalDateTime]]("updated_at")

    def * = (id, price, name, stock, uuid, description, disabled, created_at, updated_at) <> ((Product.apply _).tupled, Product.unapply)
  }

  def find(id: Long): Future[Product] = db.run {
    TableQuery[ProductsTable].filter(_.id === id).result.head
  }

  def list(): Future[Seq[Product]] = db.run {
    TableQuery[ProductsTable].sortBy(_.id.desc).result
  }

  def create(data: productEditData): Future[Any] = db.run {
    val products = TableQuery[ProductsTable]

    DBIO.seq(
      products.map(p => (p.price, p.name, p.description)) += (data.price, data.name, Option[String](data.description))
    )
  }

  def update(id: Long, data: productEditData): Future[Int] = db.run {
    val products = TableQuery[ProductsTable]
    val query = for {c <- products if c.id === id} yield (c.name, c.price, c.description)

    query.update(data.name, data.price, Option[String](data.description))
  }
}