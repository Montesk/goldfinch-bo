package controllers

import javax.inject._
import java.nio.file.Paths

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.Files

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

  def edit(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    productEditForm.bindFromRequest.fold(
      formWithErrors => {
        throw new Exception("Invalid data")
      },
      productData => {
        Await.result(productsRepo.update(id = productData.id, data = productData), Duration(10, SECONDS))
      }
    )

    Redirect(routes.ProductsController.index())
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

  private val productEditForm: Form[productEditData] = Form(
    mapping(
      "id" -> longNumber,
      "name" -> text,
      "description" -> text,
      "price" -> number
    )(productEditData.apply)(productEditData.unapply)
  )
}
