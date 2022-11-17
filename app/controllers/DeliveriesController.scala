package controllers

import javax.inject._

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import repository.deliveries.{
  DeliveriesRepository,
  DeliveryData,
  Delivery => DBDelivery
}
import repository.products.{ProductsRepository, Product => DBProduct}
import view.{Delivery, Product}

@Singleton
class DeliveriesController @Inject() (
    val controllerComponents: ControllerComponents,
    val deliveriesRepo: DeliveriesRepository,
    val productsRepo: ProductsRepository
) extends BaseController {
  def index(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(views.html.index(deliveries = Delivery.listFromDB(deliveriesList())))
  }

  def createView(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(
        views.html.index(
          deliveries = Delivery.listFromDB(deliveriesList()),
          delivery = Option(Delivery.newDelivery()),
          products = Product.listFromDB(productsList())
        )
      )
  }

  def create(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      deliveryCreateForm.bindFromRequest.fold(
        formWithErrors => {
          throw new Exception(
            s"Invalid data -> errors: ${formWithErrors.toString}"
          )
        },
        newDeliveryData => {
          deliveriesRepo.create(newDeliveryData)
        }
      )

      Redirect(routes.DeliveriesController.index())
        .flashing("success" -> s"Заказ успешно создан")
  }

  def editView(id: Long): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      ???
  }

  def edit(id: Long): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      ???
  }

  def delete(id: Long): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      deliveriesRepo.delete(id)
      Redirect(routes.DeliveriesController.index())
        .flashing("warning" -> s"Заказ ID: [$id] успешно удалён")
  }

  private val deliveryCreateForm: Form[DeliveryData] = Form(
    mapping(
      "product_id" -> longNumber,
      "quantity" -> number
    )(DeliveryData.apply)(DeliveryData.unapply)
  )

  private def deliveriesList(): Vector[(DBDelivery, DBProduct)] = {
    deliveriesRepo.list()
  }

  private def productsList(): Vector[DBProduct] = {
    productsRepo.list()
  }
}
