package view.routes

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Query
import scala.collection.mutable

import play.api.mvc.{AnyContent, Request}

trait Linker extends Routes {
  def uri(): String

  // Adds query to given or super uri
  def withQuery(uri: String = "", queryString: Option[String]): String = {
    if (uri.isEmpty) {
      return Uri.from(path = this.uri(), queryString = queryString).toString()
    }

    Uri.from(path = uri, queryString = queryString).toString()
  }

  // Adds query to request uri (preserves any existing query)
  def withQuery(req: Request[AnyContent], queryString: Option[String]): String = {
    if (req.rawQueryString.isEmpty) {
      return Uri.from(path = this.uri(), queryString = queryString).toString()
    }

    val curr = Query(req.rawQueryString)
    val next = Query(queryString.get)

    val uniqueQuery: mutable.Map[String, String] = mutable.Map()
    for (query <- curr) {
      uniqueQuery(query._1) = query._2
    }

    for (query <- next) {
      uniqueQuery(query._1) = query._2
    }

    val result = Query.apply(uniqueQuery.toMap).toString()

    Uri.from(path = this.uri(), queryString = Option(result)).toString()
  }

  def saveQuery(current: Request[AnyContent]): String = {
    val query = if (current.rawQueryString.nonEmpty) {
      Some(current.rawQueryString)
    } else {
      None
    }

    Uri.from(path = uri(), queryString = query).toString()
  }

  def saveQuery(path: String, current: Request[AnyContent]): String = {
    val query = if (current.rawQueryString.nonEmpty) {
      Some(current.rawQueryString)
    } else {
      None
    }

    Uri.from(path = path, queryString = query).toString()
  }
}

private[view] trait DynamicLinker extends Linker {
  type B

  override def uri(): String = throw new Exception(
    "unsupported static uri without params",
  )

  def uriCreate(): String = throw new Exception(s"create uri not set for ${super.getClass}")

  def uriEdit[A <: B](identifier: A): String

  def saveQueryUriTo[A <: B](
      identifier: A,
      current: Request[AnyContent],
  ): String = {
    val query = if (current.rawQueryString.nonEmpty) {
      Some(current.rawQueryString)
    } else {
      None
    }

    Uri.from(path = uriEdit[A](identifier), queryString = query).toString()
  }
}
