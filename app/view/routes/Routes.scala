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
  val settingsPath: String = Routes.settingsPath
  @deprecated("use v2 route with (settingName -> valueStr) mapping")
  val settingsUpdatePath: String = Routes.settingsUpdatePath
  val settingsUpdatePathV2: String = Routes.settingsUpdatePathV2

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
  val settingsPath: String = routes.SettingsController.index().url
  @deprecated("use v2 route with (settingName -> valueStr) mapping")
  val settingsUpdatePath: String = routes.SettingsController.edit().url
  val settingsUpdatePathV2: String = routes.SettingsController.editV2().url

  protected val ProductGroupPath: String = "product"
  protected val ProductEditGroupPath: String = "product/edit"
}
