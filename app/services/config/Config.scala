package services.config

import pkg.dto.{config => dto}
import scala.concurrent.duration.Duration

object Config {
  var loaded: Option[dto.Config] = None

  def load(yaml: String): Unit = {
    loaded = Option(dto.Config(yaml).withDefaults())
  }

  def appSettingsVersion(): String = {
    loaded.get.app.settingsVersion
  }

  def appTheme(): String = {
    loaded.get.app.theme.get
  }

  def appSettingsFilePath(): String = {
    loaded.get.app.settingsFilePath.get
  }

  def appLanguage(): String = {
    loaded.get.app.language.get
  }

  def appDemoMode(): Boolean = {
    loaded.get.app.demoMode.get
  }

  def appStartPage(): String = {
    loaded.get.app.startPage.get
  }

  def repositoriesDefaultDbWaitTimeout(): Duration = {
    Duration(loaded.get.repositories.default.dbWaitTimeout)
  }

  def repositoriesDeliveryDbWaitTimeout(): Duration = {
    Duration(loaded.get.repositories.delivery.get.dbWaitTimeout)
  }

  def repositoriesProductDbWaitTimeout(): Duration = {
    Duration(loaded.get.repositories.product.get.dbWaitTimeout)
  }

  def repositoriesOrderDbWaitTimeout(): Duration = {
    Duration(loaded.get.repositories.order.get.dbWaitTimeout)
  }

  def modulesDefaultPerPage(): Int = {
    loaded.get.modules.default.perPage
  }

  def modulesProductPerPage(): Int = {
    loaded.get.modules.product.get.perPage
  }

  def modulesProductView(): String = {
    loaded.get.modules.product.get.view
  }
}

private case class App(
    theme: String,
    settingsFilePath: String,
    settingsVersion: String,
    language: String,
    demoMode: Boolean,
    startPage: String,
)

private trait Repository {
  val dbWaitTimeout: Duration
}

private case class Repositories(
    defaults: Repository,
    delivery: Repository,
    product: Repository,
    order: Repository,
)

private trait Module {
  val perPage: Int
}

private case class DefaultModule(perPage: Int) extends Module
private case class ModuleProduct(override val perPage: Int, view: String) extends Module

private case class Modules(
    defaults: Module,
    products: ModuleProduct,
)
