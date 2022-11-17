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
  type B

  override def uri(): String = throw new Exception(
    "unsupported static uri without params"
  )

  def uriTo[A <: B](identifier: A): String

  def saveQueryUriTo[A <: B](
      identifier: A,
      current: Request[AnyContent]
  ): String = {
    val query = if (current.rawQueryString.nonEmpty) {
      Some(current.rawQueryString)
    } else {
      None
    }

    Uri.from(path = uriTo[A](identifier), queryString = query).toString()
  }
}
