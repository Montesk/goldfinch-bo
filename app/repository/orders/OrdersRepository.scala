package repository.orders

import java.time._
import javax.inject.{Inject, Singleton}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import repository.Repository

@Singleton
class OrdersRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
    extends Repository {
  val DefaultDBTimeout: Duration = withDefaultTimeout(config.order)

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class OrderTable(tag: Tag) extends Table[Order](tag, "orders") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def status = column[String]("status", O.Default("new"))

    def costumer_id = column[Long]("customer_id")

    def created_at = column[LocalDateTime]("created_at")

    def updated_at = column[LocalDateTime]("updated_at")

    def * = (
      id,
      status,
      costumer_id,
      created_at,
      updated_at
    ) <> ((Order.apply _).tupled, Order.unapply)
  }

  private val orders = TableQuery[OrderTable]

  def list(): Vector[Order] = {
    Await.result(execSelect(), DefaultDBTimeout).toVector
  }

  private def execSelect(): Future[Seq[Order]] = db.run {
    orders.result
  }
}
