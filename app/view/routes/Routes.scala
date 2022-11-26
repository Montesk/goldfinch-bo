package view.routes

import controllers.routes

trait Routes {
  val homePath: String = Routes.homePath
  val ordersPath: String = Routes.ordersPath
  val productsPath: String = Routes.productsPath
  val productEditPath: Long => String = Routes.productEditPath
  val productCreatePath: String = Routes.productCreatePath
  val deliveriesPath: String = Routes.deliveriesPath
  val deliveryPath: Long => String = Routes.deliveryPath
  val deliveryCreatePath: String = Routes.deliveryCreatePath
  val deliveryDeletePath: Long => String = Routes.deliveryDeletePath
  val settingsUpdatePath: String = Routes.settingsUpdatePath

  protected val ProductGroupPath: String = Routes.ProductGroupPath
  protected val ProductEditGroupPath: String = Routes.ProductEditGroupPath
}

object Routes {
  val homePath: String = routes.HomeController.index().url
  val ordersPath: String = routes.OrdersController.index().url
  val productsPath: String = routes.ProductsController.index().url
  val productEditPath: Long => String = (productId) => routes.ProductsController.editView(productId).url
  val productCreatePath: String = routes.ProductsController.create().url
  val deliveriesPath: String = routes.DeliveriesController.index().url
  val deliveryPath: Long => String = (deliveryId: Long) => routes.DeliveriesController.editView(deliveryId: Long).url
  val deliveryCreatePath: String = routes.DeliveriesController.create().url
  val deliveryDeletePath: Long => String = (deliveryId: Long) =>
    routes.DeliveriesController.delete(deliveryId: Long).url
  val settingsUpdatePath: String = routes.SettingsController.edit().url

  protected val ProductGroupPath: String = "product"
  protected val ProductEditGroupPath: String = "product/edit"
}
