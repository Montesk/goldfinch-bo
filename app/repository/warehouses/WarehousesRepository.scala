package repository.warehouses

import java.time._
import javax.inject.{Inject, Singleton}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import services.config.Config

case class WarehouseData(
    name: String,
    description: Option[String],
)

@Singleton
class WarehousesRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) {
  val DefaultDBTimeout: Duration = Config.repositoriesOrderDbWaitTimeout()

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class WarehouseTable(tag: Tag) extends Table[Warehouse](tag, "warehouses") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def description = column[Option[String]]("description")

    def deleted = column[Boolean]("deleted", O.Default(false))

    def disabled = column[Boolean]("disabled", O.Default(false))

    def created_at = column[Option[LocalDateTime]]("created_at")

    def updated_at = column[Option[LocalDateTime]]("updated_at")

    def * = (
      id,
      name,
      description,
      deleted,
      disabled,
      created_at,
      updated_at,
    ) <> ((Warehouse.apply _).tupled, Warehouse.unapply)
  }

  private val warehouses = TableQuery[WarehouseTable]

  def list(): Vector[Warehouse] = {
    Await.result(execSelect(), DefaultDBTimeout).toVector
  }

  def create(data: WarehouseData): Unit = {
    Await.result(execCreate(data), DefaultDBTimeout)
  }

  private def execCreate(data: WarehouseData): Future[Unit] = db.run {
    DBIO.seq(
      warehouses.map(p => (p.name, p.description)) += (data.name, data.description),
    )
  }

  private def execSelect(): Future[Seq[Warehouse]] = db.run {
    warehouses.result
  }
}
