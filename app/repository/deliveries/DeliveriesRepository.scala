package repository.deliveries

import java.time._
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import play.api.db.slick.DatabaseConfigProvider

import repository.products.{Products, Product}

case class DeliveryData(
                         product_id: Long,
                         quantity: Int,
                       )

class Deliveries(tag: Tag) extends Table[Delivery](tag, "deliveries") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def product_id = column[Long]("product_id")

  def quantity = column[Int]("quantity")

  def created_at = column[Option[LocalDateTime]]("created_at")

  def updated_at = column[Option[LocalDateTime]]("updated_at")

  def productsFk = foreignKey("deliveries_product_id_fkey", product_id, TableQuery[Products])(_.id)

  def * = (id, product_id, quantity, created_at, updated_at) <> ((Delivery.apply _).tupled, Delivery.unapply)
}

@Singleton
class DeliveriesRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._

  val deliveries = TableQuery[Deliveries]
  val products = TableQuery[Products]

  def find(id: Long): Future[Delivery] = db.run {
    deliveries
      .filter(_.id === id)
      .result
      .head
  }

  def list(): Future[Seq[(Delivery, Product)]] = db.run {
    val withJoin = for {
      delivery <- deliveries.sortBy(_.created_at.desc)
      product <- delivery.productsFk
    } yield (delivery, product)

    withJoin.result
  }

  def create(data: DeliveryData): Future[Any] = db.run {
    DBIO.seq(
      deliveries.map(p => (p.quantity, p.product_id)) += (data.quantity, data.product_id)
    )
  }

  def update(id: Long): Future[Int] = db.run {
    ???
  }

  def delete(id: Long, soft: Boolean = true): Future[Int] = db.run {
    ???
  }
}