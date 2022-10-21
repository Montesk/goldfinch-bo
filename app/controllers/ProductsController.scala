package controllers

import javax.inject._

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import repository.products.{ProductsRepository, productEditData, Product => DBProduct}
import view.Product

@Singleton
class ProductsController @Inject()(
                                    val controllerComponents: ControllerComponents,
                                    val productsRepo: ProductsRepository
                                  ) extends BaseController {
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(products = Product.listFromDB(productsList())))
  }

  def createView(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(
      products = Product.listFromDB(productsList()),
      product = Option[Product](Product.create())
    ))
  }

  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    ???
  }

  def editView(id: Long): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(
      products = Product.listFromDB(productsList()),
      product = Option[Product](Product.fromDB(product(id)))
    ))
  }

  def edit(id: Long): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    productEditForm.bindFromRequest.fold(
      formWithErrors => {
        throw new Exception("Invalid data")
      },
      productData => {
        Await.result(productsRepo.update(id = id, data = productData), Duration(10, SECONDS))
      }
    )

    Redirect(routes.ProductsController.index())
  }

  private def product(id: Long): DBProduct = {
    Await.result(productsRepo.find(id), Duration(10, SECONDS))
  }

  private def productsList(): Seq[DBProduct] = {
    Await.result(productsRepo.list(), Duration(10, SECONDS))
  }

  private val productEditForm: Form[productEditData] = Form(
    mapping(
      "name" -> text,
      "description" -> text,
      "price" -> number
    )(productEditData.apply)(productEditData.unapply)
  )
}
