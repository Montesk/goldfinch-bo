package controllers

import javax.inject._
import java.nio.file.Paths

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.Files

import repository.products.{ProductsRepository, productData, Product => DBProduct}
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
    productEditForm.bindFromRequest.fold(
      formWithErrors => {
        throw new Exception("Invalid data")
      },
      productData => {
        Await.result(productsRepo.create(data = productData), Duration(10, SECONDS))
      }
    )

    Redirect(routes.ProductsController.index())
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

  def delete(id: Long): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Await.result(productsRepo.delete(id = id, soft = true), Duration(10, SECONDS))
    Redirect(routes.ProductsController.index())
  }

  private def product(id: Long): DBProduct = {
    Await.result(productsRepo.find(id), Duration(10, SECONDS))
  }

  def upload(): Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
    request.body
      .file("image")
      .map { picture =>

        val filename = Paths.get(picture.filename).getFileName
        val fileSize = picture.fileSize
        val contentType = picture.contentType

        picture.ref.copyTo(Paths.get(s"./public/images/upload/$filename"), replace = true)

        Ok("File uploaded")
      }.getOrElse(throw new Exception("Invalid data"))
  }

  private def productsList(): Seq[DBProduct] = {
    Await.result(productsRepo.list(), Duration(10, SECONDS))
  }

  private val productEditForm: Form[productData] = Form(
    mapping(
      "name" -> text,
      "description" -> text,
      "price" -> number
    )(productData.apply)(productData.unapply)
  )
}
