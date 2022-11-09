package view

import controllers.routes

trait Routes {
  val HomePath: String = Routes.HomePath
  val OrdersPath: String = Routes.OrdersPath
  val ProductsPath: String = Routes.ProductsPath
  val ProductPath: Long => String = Routes.ProductPath
  val ProductCreatePath: String = Routes.ProductCreatePath
  val DeliveriesPath: String = Routes.DeliveriesPath
  val DeliveryPath: Long => String = Routes.DeliveryPath
  val DeliveryCreatePath: String = Routes.DeliveryCreatePath
}

object Routes {
  val HomePath: String = routes.HomeController.index().url
  val OrdersPath: String = routes.OrdersController.index().url
  val ProductsPath: String = routes.ProductsController.index().url
  val ProductPath: Long => String = (productId: Long) => routes.ProductsController.editView(productId: Long).url
  val ProductCreatePath: String = routes.ProductsController.create().url
  val DeliveriesPath: String = routes.DeliveriesController.index().url
  val DeliveryPath: Long => String = (deliveryId: Long) => routes.DeliveriesController.editView(deliveryId: Long).url
  val DeliveryCreatePath: String = routes.DeliveriesController.create().url
}
