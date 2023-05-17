package view.routes

import controllers.routes

trait Routes {
  val homePath: String = Routes.homePath
  val ordersPath: String = Routes.ordersPath
  val productsPath: String = Routes.productsPath
  val productEditPath: Long => String = Routes.productEditPath
  val productCreatePath: String = Routes.productCreatePath
  val productViewPath: Long => String = Routes.productViewPath
  val deliveriesPath: String = Routes.deliveriesPath
  val deliveryPath: Long => String = Routes.deliveryEditPath
  val deliveryCreatePath: String = Routes.deliveryCreatePath
  val deliveryDeletePath: Long => String = Routes.deliveryDeletePath
  val settingsPath: String = Routes.settingsPath
  val settingsUpdatePathV2: String = Routes.settingsUpdatePathV2
  val warehousesPath: String = Routes.warehousesPath
  val warehouseCreatePath: String = Routes.warehouseCreatePath

  protected val ProductGroupPath: String = Routes.ProductGroupPath
  protected val ProductEditGroupPath: String = Routes.ProductEditGroupPath

  @deprecated("use v2 route with (settingName -> valueStr) mapping")
  val settingsUpdatePath: String = Routes.settingsUpdatePath
}

object Routes {
  val homePath: String = routes.HomeController.index().url
  val ordersPath: String = routes.OrdersController.index().url
  val productsPath: String = routes.ProductsController.index().url
  val productEditPath: Long => String = productId => routes.ProductsController.editView(productId).url
  val productCreatePath: String = routes.ProductsController.create().url
  val productViewPath: Long => String = productId => routes.ProductsController.view(productId).url
  val deliveriesPath: String = routes.DeliveriesController.index().url
  val deliveryEditPath: Long => String = (deliveryId: Long) =>
    routes.DeliveriesController.editView(deliveryId: Long).url
  val deliveryCreatePath: String = routes.DeliveriesController.create().url
  val deliveryDeletePath: Long => String = (deliveryId: Long) =>
    routes.DeliveriesController.delete(deliveryId: Long).url
  val settingsPath: String = routes.SettingsController.index().url
  val settingsUpdatePathV2: String = routes.SettingsController.editV2().url
  val warehousesPath: String = routes.WarehousesController.index().url
  val warehouseCreatePath: String = routes.WarehousesController.create().url

  protected val ProductGroupPath: String = "product"
  protected val ProductEditGroupPath: String = "product/edit"

  @deprecated("use v2 route with (settingName -> valueStr) mapping")
  val settingsUpdatePath: String = routes.SettingsController.edit().url
}
