package controllers

import javax.inject._

import play.api.mvc._

import repository.orders.{OrdersRepository, Order => DBOrder}
import view.Order

@Singleton
class OrdersController @Inject() (
    val controllerComponents: ControllerComponents,
    val ordersRepo: OrdersRepository
) extends BaseController {
  def index(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(views.html.index(orders = Order.listFromDB(ordersList())))
  }

  private def ordersList(): Vector[DBOrder] = {
    ordersRepo.list()
  }
}
