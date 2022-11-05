package view

import akka.http.scaladsl.model.Uri

import play.api.mvc.{AnyContent, Request}

trait Linker extends Routes {
  def uri(): String

  def withQuery(uri: String = "", queryString: Option[String]): String = {
    if (uri.isEmpty) {
      return Uri.from(path = this.uri(), queryString = queryString).toString()
    }

    Uri.from(path = uri, queryString = queryString).toString()
  }

  def saveQuery(current: Request[AnyContent]): String = {
    val query = if (current.rawQueryString.nonEmpty) {
      Some(current.rawQueryString)
    } else {
      None
    }

    Uri.from(path = uri(), queryString = query).toString()
  }
}

trait DynamicLinker extends Linker {
  override def uri(): String = throw new Exception("unsupported static uri without params")

  type U

  def uriTo[T <: U](identifier: T): String

  def saveQueryUriTo[T <: U](identifier: T, current: Request[AnyContent]): String = {
    val query = if (current.rawQueryString.nonEmpty) {
      Some(current.rawQueryString)
    } else {
      None
    }

    Uri.from(path = uriTo[T](identifier), queryString = query).toString()
  }
}
