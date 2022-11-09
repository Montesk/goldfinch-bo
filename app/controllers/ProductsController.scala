package controllers

import javax.inject._
import com.google.common.io.{Files => helperFiles}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.Files

import repository.products.{ProductsRepository, ProductData, Product => DBProduct}
import services.assets.Manager
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
      product = Option[Product](Product.newProduct())
    ))
  }

  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    productEditForm.bindFromRequest.fold(
      formWithErrors => {
        throw new Exception(s"Invalid data ${formWithErrors.toString}")
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
        throw new Exception(s"Invalid data ${formWithErrors.toString}")
      },
      productData => {
        Await.result(productsRepo.update(id = id, data = productData), Duration(10, SECONDS))

        val product = this.product(id)

        for (imageToDelete <- productData.deleteFiles) {
          deleteImage(product = product, image = imageToDelete)
        }

        for (image <- productData.files) {
          persistImage(product = product, fileName = (lastImg(Product.fromDB(product).files) + 1).toString, image = image)
        }

        if (productData.titleImage.nonEmpty) {
          persistImage(product = product, isMain = true, image = productData.titleImage.getOrElse(""))
        }
      }
    )

    Redirect(routes.ProductsController.index())
  }

  def delete(id: Long): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Await.result(productsRepo.delete(id = id), Duration(10, SECONDS))
    Redirect(routes.ProductsController.index())
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
      assetsManager.saveTemporaryFile(file = file.ref, productUUID, file.filename)
      uploaded = file.filename
    }

    Ok(uploaded)
  }

  private def product(id: Long): DBProduct = {
    Await.result(productsRepo.find(id), Duration(10, SECONDS))
  }

  private def productsList(): Seq[DBProduct] = {
    Await.result(productsRepo.list(), Duration(10, SECONDS))
  }

  private val productEditForm: Form[ProductData] = Form(
    mapping(
      "name" -> text,
      "description" -> text,
      "price" -> number,
      "files" -> list(text),
      "title-image" -> optional(text),
      "delete-files" -> list(text),
    )(ProductData.apply)(ProductData.unapply)
  )

  private def lastImg(files: List[String]): Int = {
    var result = 0

    for (f <- files) {
      val got = helperFiles.getNameWithoutExtension(f).toInt
      if (got > result) {
        result = got
      }
    }

    result
  }

  private def persistImage(product: DBProduct, image: String, isMain: Boolean = false, fileName: String = ""): Unit = {
    val extension = helperFiles.getFileExtension(image)
    if (isMain) {
      assetsManager.persistTempFile(product.uuid.get.toString, image, Product.MainImageFileName + "." + extension)
    } else {
      assetsManager.persistTempFile(product.uuid.get.toString, image, fileName + "." + extension)
    }
  }

  private def deleteImage(product: DBProduct, image: String): Unit = {
    assetsManager.deleteFile(product.uuid.get.toString, image)
  }
}
