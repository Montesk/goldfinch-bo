package repository.products

import java.time._
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider

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

    def created_at = column[LocalDateTime]("created_at")

    def updated_at = column[LocalDateTime]("updated_at")

    def * = (id, price, name, stock, created_at, updated_at) <> ((Product.apply _).tupled, Product.unapply)
  }

  def list(): Future[Seq[Product]] = db.run {
    TableQuery[ProductsTable].result
  }
}