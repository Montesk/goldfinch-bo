package view

import controllers.routes

trait Routes {
  val ProductsPath: String = Routes.ProductsPath
  val ProductPath: Long => String = Routes.ProductPath
}

object Routes {
  val ProductsPath: String = routes.ProductsController.index().url
  val ProductPath: Long => String = (productId: Long) => routes.ProductsController.editView(productId: Long).url
}
