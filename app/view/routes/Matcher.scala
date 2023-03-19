package view.routes

import play.api.mvc.{AnyContent, Request}
import view.pagination.Pagination

object Matcher extends Routes with Pagination {
  def isHomePage(implicit req: Request[AnyContent]): Boolean = {
    req.path == homePath
  }

  def isOrdersPage(implicit req: Request[AnyContent]): Boolean = {
    req.path == ordersPath
  }

  def isProductsPage(implicit req: Request[AnyContent]): Boolean = {
    req.path == productsPath
  }

  def isProductCreatePage(implicit req: Request[AnyContent]): Boolean = {
    req.path == productCreatePath
  }

  def isProductViewPage(implicit req: Request[AnyContent]): Boolean = {
    req.path.contains(ProductGroupPath)
  }

  def isInProductGroupPage(implicit req: Request[AnyContent]): Boolean = {
    req.path.contains(ProductGroupPath)
  }

  def isInProductEditGroupPage(implicit req: Request[AnyContent]): Boolean = {
    req.path.contains(ProductEditGroupPath)
  }

  def isWarehouseCreatePage(implicit req: Request[AnyContent]): Boolean = {
    req.path == deliveryCreatePath
  }

  def isDeliveriesPage(implicit req: Request[AnyContent]): Boolean = {
    req.path == deliveriesPath
  }

  def isSettingsPage(implicit req: Request[AnyContent]): Boolean = {
    req.path == settingsPath
  }
}
