package controllers

import javax.inject._

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.Files

import repository.products.{ProductsRepository, productData, Product => DBProduct}
import services.assets.{Manager}
import view.Product


@Singleton
class ProductsController @Inject()(
                                    val controllerComponents: ControllerComponents,
                                    val productsRepo: ProductsRepository,
                                    val assetsManager: Manager,
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

  private def persistImage(productId: Long, image: String, isMain: Boolean = false, idx: Int = 0): Unit = {
    val product = this.product(productId)
    val extension = image.split("\\.")(1)
    if (isMain) {
      assetsManager.PersistTempFile(product.uuid.get.toString, image, "main" + "." + extension)
    } else {
      assetsManager.PersistTempFile(product.uuid.get.toString, image, (idx + 1).toString + "." + extension)
    }
  }

  def edit(id: Long): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    productEditForm.bindFromRequest.fold(
      formWithErrors => {
        throw new Exception("Invalid data " + formWithErrors.toString)
      },
      productData => {
        Await.result(productsRepo.update(id = id, data = productData), Duration(10, SECONDS))

        for ((image, idx) <- productData.files.zipWithIndex) {
          persistImage(productId = id, idx = idx, image = image)
        }

        if (productData.titleImage.nonEmpty) {
          persistImage(productId = id, isMain = true, image = productData.titleImage.getOrElse(""))
        }
      }
    )

    Redirect(routes.ProductsController.index())
  }

  def delete(id: Long): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Await.result(productsRepo.delete(id = id), Duration(10, SECONDS))
    Redirect(routes.ProductsController.index())
  }

  private def product(id: Long): DBProduct = {
    Await.result(productsRepo.find(id), Duration(10, SECONDS))
  }

  /**
   *
   * Params: {
   * <body> "files": [fileObj] // :WARNING: currently supported single file upload
   * <query> "uuid": Option[UUID] // if not passed it is assumed it is the new product
   * <query> "csrfTokenName": "csrfTokenValue"
   * }
   *
   * @return
   */
  def upload(): Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
    val productUUID = request.getQueryString("uuid").get

    var uploaded: String = ""

    for (file <- request.body.files) {
      assetsManager.SaveTemporaryFile(file = file.ref, productUUID, file.filename)
      uploaded = file.filename
    }

    Ok(uploaded)
  }

  private def productsList(): Seq[DBProduct] = {
    Await.result(productsRepo.list(), Duration(10, SECONDS))
  }

  private val productEditForm: Form[productData] = Form(
    mapping(
      "name" -> text,
      "description" -> text,
      "price" -> number,
      "files" -> list(text),
      "title-image" -> optional(text),
    )(productData.apply)(productData.unapply)
  )
}
