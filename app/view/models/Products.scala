package view.models

import akka.http.scaladsl.model.Uri
import play.api.mvc.{AnyContent, Request}
import view.routes.Linker
import services.settings.{Settings => AppSettings}

case class Products(list: Vector[Product], total: Int = 0)

object Products extends Linker {
  val tableView = "table"
  val albumView = "album"

  private[view] val defaultView = tableView

  final case class UnsupportedViewException(message: String) extends Exception {
    new Exception(s"unsupported products page view ${message}")
  }

  def uri(): String = {
    Uri.from(path = productsPath).toString()
  }

  def isTableView(req: Request[AnyContent]): Boolean = {
    getViewFromRequest(req = req).eq(tableView)
  }

  def isAlbumView(req: Request[AnyContent]): Boolean = {
    getViewFromRequest(req = req).eq(albumView)
  }

  override def saveQuery(current: Request[AnyContent]): String = {
    val query = if (current.rawQueryString.nonEmpty) {
      Some(current.rawQueryString)
    } else {
      Some(s"perPage=${AppSettings.moduleProductsPerPage().toString}")
    }

    Uri.from(path = uri(), queryString = query).toString()
  }

  private[view] def getViewFromRequest(req: Request[AnyContent]): String = {
    if (req.getQueryString("view").isEmpty) {
      return defaultView
    }

    req.getQueryString("view").get match {
      case `tableView` => tableView
      case `albumView` => albumView
      case _           => throw UnsupportedViewException(req.getQueryString("view").get)
    }
  }
}
