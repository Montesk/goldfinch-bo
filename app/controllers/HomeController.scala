package controllers

import javax.inject._

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

import play.api.mvc._

import repository.{Order, OrderRepository}

@Singleton
class HomeController @Inject()(
                                val controllerComponents: ControllerComponents,
                                val orderRepo: OrderRepository
) extends BaseController {
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(orders = ordersList()))
  }

  private def ordersList(): Seq[Order] = {
    Await.result(orderRepo.list(), Duration(10, SECONDS))
  }
}
