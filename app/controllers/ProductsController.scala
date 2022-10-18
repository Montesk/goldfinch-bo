package controllers

import javax.inject._
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

import play.api.mvc._

import repository.products.{ProductsRepository, Product => DBProduct}
import view.Product

@Singleton
class ProductsController @Inject()(
                                    val controllerComponents: ControllerComponents,
                                    val productsRepo: ProductsRepository
                                  ) extends BaseController {
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(products = Product.listFromDB(productsList())))
  }

  private def productsList(): Seq[DBProduct] = {
    Await.result(productsRepo.list(), Duration(10, SECONDS))
  }
}
