package controllers

import javax.inject._

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import repository.warehouses.{WarehousesRepository, Warehouse => DBWarehouse, WarehouseData}
import view.models.Warehouse

@Singleton
class WarehousesController @Inject() (
    val controllerComponents: ControllerComponents,
    val warehousesRepo: WarehousesRepository,
) extends BaseController
    with I18n {
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(warehouses = Warehouse.listFromDB(warehouseList())))
  }

  def createView(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(
      views.html.index(),
    )
  }

  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    warehouseCreateForm
      .bindFromRequest()
      .fold(
        formWithErrors => {
          throw new Exception(
            s"Invalid data -> errors: ${formWithErrors.toString}",
          )
        },
        warehouseData => {
          warehousesRepo.create(warehouseData)
        },
      )

    Redirect(routes.WarehousesController.index())
      .flashing("success" -> s"Склад успешно создан")
  }

  def editView(id: Long): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    ???
  }

  def edit(id: Long): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    ???
  }

  def delete(id: Long): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    ???
  }

  private def warehouseList(): Vector[DBWarehouse] = {
    warehousesRepo.list()
  }

  private val warehouseCreateForm: Form[WarehouseData] = Form(
    mapping(
      "name" -> nonEmptyText,
      "description" -> optional(text),
    )(WarehouseData.apply)(WarehouseData.unapply),
  )
}
