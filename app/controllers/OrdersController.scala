package controllers

import play.api.mvc._
import repository.{OrderRepository, Order => DBOrder}
import view.home.Order

import javax.inject._
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

@Singleton
class OrdersController @Inject()(
                                  val controllerComponents: ControllerComponents,
                                  val orderRepo: OrderRepository
                                ) extends BaseController {
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(orders = Order.listFromDB(ordersList())))
  }

  private def ordersList(): Seq[DBOrder] = {
    Await.result(orderRepo.list(), Duration(10, SECONDS))
  }
}
