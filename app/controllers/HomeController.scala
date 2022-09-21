package controllers

import javax.inject._

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

import play.api.mvc._

import repository.{Order => DBOrder, OrderRepository}
import view.home.Order

@Singleton
class HomeController @Inject()(
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
