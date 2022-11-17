package repository.deliveries

import java.time._
import javax.inject.{Inject, Singleton}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import play.api.db.slick.DatabaseConfigProvider

import repository.Repository
import repository.products.{Product, Products}

case class DeliveryData(
    product_id: Long,
    quantity: Int
)

class Deliveries(tag: Tag) extends Table[Delivery](tag, "deliveries") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def product_id = column[Long]("product_id")

  def quantity = column[Int]("quantity")

  def deleted = column[Boolean]("deleted", O.Default(false))

  def created_at = column[Option[LocalDateTime]]("created_at")

  def updated_at = column[Option[LocalDateTime]]("updated_at")

  def productsFk =
    foreignKey("deliveries_product_id_fkey", product_id, TableQuery[Products])(
      _.id
    )

  def * = (
    id,
    product_id,
    quantity,
    created_at,
    updated_at
  ) <> ((Delivery.apply _).tupled, Delivery.unapply)
}

@Singleton
class DeliveriesRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
    extends Repository {
  val DefaultDBTimeout: Duration = withDefaultTimeout(config.delivery)

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._

  val deliveries = TableQuery[Deliveries]

  def find(id: Long): Delivery = {
    Await.result(execFind(id), DefaultDBTimeout)
  }

  def list(): Vector[(Delivery, Product)] = {
    Await.result(execSelect(), DefaultDBTimeout).toVector
  }

  def create(data: DeliveryData): Unit = {
    Await.result(execCreate(data), DefaultDBTimeout)
  }

  def update(id: Long): Int = {
    ???
  }

  def delete(id: Long, soft: Boolean = true): Int = {
    Await.result(execDelete(id, soft), DefaultDBTimeout)
  }

  private def execSelect(): Future[Seq[(Delivery, Product)]] = db.run {
    val withJoin = for {
      delivery <- deliveries
        .filter(_.deleted === false)
        .sortBy(_.created_at.desc)
      product <- delivery.productsFk
    } yield (delivery, product)

    withJoin.result
  }

  private def execFind(id: Long): Future[Delivery] = db.run {
    deliveries
      .filter(_.id === id)
      .result
      .head
  }

  private def execCreate(data: DeliveryData): Future[Unit] = db.run {
    DBIO.seq(
      deliveries.map(p =>
        (p.quantity, p.product_id)
      ) += (data.quantity, data.product_id)
    )
  }

  private def execDelete(id: Long, soft: Boolean): Future[Int] = db.run {
    if (soft) {
      val query = for { d <- deliveries if d.id === id } yield d.deleted
      query.update(true)
    } else {
      deliveries.filter(_.id === id).delete
    }
  }
}
