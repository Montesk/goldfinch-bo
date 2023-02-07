package pkg.dto.config

import cats.syntax.either._
import io.circe.{Json, ParsingFailure}
import io.circe.generic.auto._
import io.circe.yaml.parser

object Constants {
  object App {
    val theme: String = "green"
    val lang: String = "en"
    val settingsFilePath: String = "settings.xml"
    val startPage: String = "/"
    val demoMode: Boolean = false
  }

  object Modules {
    object Product {
      val view: String = "table"
    }
  }
}

object Config {
  def apply(yaml: String): Config = {
    val json: Either[ParsingFailure, Json] = parser.parse(yaml)
    val fromFile = json.flatMap(_.as[Config]).valueOr(throw _)

    new Config(
      app = fromFile.app,
      repositories = fromFile.repositories,
      modules = fromFile.modules,
    )
  }
}

case class Config(
    app: App,
    repositories: Repositories,
    modules: Modules,
) {
  def withDefaults(): Config = {
    Config(
      app = app.withDefaults(),
      repositories = repositories.withDefaults(),
      modules = modules.withDefaults(),
    )
  }
}

case class App(
    settingsVersion: String,
    theme: Option[String],
    settingsFilePath: Option[String],
    language: Option[String],
    demoMode: Option[Boolean],
    startPage: Option[String],
) {
  private[config] def withDefaults(): App = {
    App(
      settingsVersion,
      theme.orElse(Option(Constants.App.theme)),
      settingsFilePath.orElse(Option(Constants.App.settingsFilePath)),
      language.orElse(Option(Constants.App.lang)),
      demoMode.orElse(Option(Constants.App.demoMode)),
      startPage.orElse(Option(Constants.App.startPage)),
    )
  }
}

case class Repositories(
    default: RepositoryDefault,
    delivery: Option[RepositoryDelivery],
    product: Option[RepositoryProduct],
    order: Option[RepositoryOrder],
) {
  private[config] def withDefaults(): Repositories = {
    Repositories(
      default,
      delivery.orElse(Option(RepositoryDelivery(dbWaitTimeout = default.dbWaitTimeout))),
      product.orElse(Option(RepositoryProduct(dbWaitTimeout = default.dbWaitTimeout))),
      order.orElse(Option(RepositoryOrder(dbWaitTimeout = default.dbWaitTimeout))),
    )
  }
}

trait RepositoryBase {
  val dbWaitTimeout: String
}

case class RepositoryDefault(override val dbWaitTimeout: String) extends RepositoryBase {}
case class RepositoryDelivery(override val dbWaitTimeout: String) extends RepositoryBase {}
case class RepositoryProduct(override val dbWaitTimeout: String) extends RepositoryBase {}
case class RepositoryOrder(override val dbWaitTimeout: String) extends RepositoryBase {}

case class Modules(
    default: ModuleDefault,
    product: Option[ModuleProducts],
) {
  private[config] def withDefaults(): Modules = {
    Modules(
      default = default,
      product = product.orElse(
        Option(
          ModuleProducts(
            perPage = default.perPage,
            view = Constants.Modules.Product.view,
          ),
        ),
      ),
    )
  }
}

trait ModuleBase {
  val perPage: Int
}

case class ModuleDefault(override val perPage: Int) extends ModuleBase
case class ModuleProducts(override val perPage: Int, view: String) extends ModuleBase
