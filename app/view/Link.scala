package view

import akka.http.scaladsl.model.Uri

import play.api.mvc.{AnyContent, Request}

import controllers.routes

trait Link {
  val ProductsPath: String = routes.ProductsController.index().url
  val ProductPath: Long => String = (productId: Long) => routes.ProductsController.editView(productId: Long).url

  def uri(): String

  def withQuery(uri: String = "", queryString: Option[String]): String = {
    if (uri.isEmpty) {
      return Uri.from(path = this.uri(), queryString = queryString).toString()
    }

    Uri.from(path = uri, queryString = queryString).toString()
  }
}

trait DynamicLink extends Link {
  override def uri(): String = throw new Exception("unsupported static uri without params")

  def uriTo[T <: AnyVal](identifier: T): String

  def saveQueryUriTo[T <: AnyVal](identifier: T, current: Request[AnyContent]): String = {
    val query = if (current.rawQueryString.nonEmpty) {
      Some(current.rawQueryString)
    } else {
      None
    }

    Uri.from(path = uriTo[T](identifier), queryString = query).toString()
  }
}
