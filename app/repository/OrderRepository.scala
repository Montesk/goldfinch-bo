package repository

import java.time._
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

@Singleton
class OrderRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class OrderTable(tag: Tag) extends Table[Order](tag, "orders") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def status = column[String]("status", O.Default("new"))
    def costumer_id = column[Long]("customer_id")
    def created_at = column[LocalDateTime]("created_at")
    def updated_at = column[LocalDateTime]("updated_at")

    def * = (id, status, costumer_id, created_at, updated_at) <> ((Order.apply _).tupled, Order.unapply)
  }

  private val orders = TableQuery[OrderTable]

  def list(): Future[Seq[Order]] = db.run {
    orders.result
  }
}