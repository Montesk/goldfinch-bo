package pkg.dto.config

import io.circe.DecodingFailure
import org.scalatestplus.play.PlaySpec

class ConfigSpec extends PlaySpec {
  val configFilePath: String = "test/pkg/dto/config/stubs/read/config.yml"
  val requiredFieldsFilePath: String = "test/pkg/dto/config/stubs/read/required.yml"

  // Actual file values
  val expectAppTheme: String = "goldfinch"
  val expectAppSettingsFilePath: String = "test/settings.xml"
  val expectAppLanguage: String = "nl"
  val expectAppSettingsVersion: String = "0.2.14"
  val expectAppDemoMode: Boolean = true
  val expectAppStartPage: String = "/warehouse"

  val expectRepositoriesDefaultDbWaitTimeout: String = "10s"
  val expectRepositoriesDeliveryDbWaitTimeout: String = "15s"
  val expectRepositoriesProductDbWaitTimeout: String = "3s"
  val expectRepositoriesOrderDbWaitTimeout: String = "5s"

  val expectModulesDefaultPerPage: Int = 20
  val expectModulesProductPerPage: Int = 7
  val expectModulesProductView: String = "album"

  "Config" must {
    "parse all fields" in {
      val got = Config(readFile(path = configFilePath))

      got.app.settingsVersion mustEqual expectAppSettingsVersion
      got.app.theme.get mustEqual expectAppTheme
      got.app.settingsFilePath.get mustEqual expectAppSettingsFilePath
      got.app.language.get mustEqual expectAppLanguage
      got.app.demoMode.get mustEqual expectAppDemoMode
      got.app.startPage.get mustEqual expectAppStartPage

      got.repositories.default.dbWaitTimeout mustEqual expectRepositoriesDefaultDbWaitTimeout
      got.repositories.delivery.get.dbWaitTimeout mustEqual expectRepositoriesDeliveryDbWaitTimeout
      got.repositories.product.get.dbWaitTimeout mustEqual expectRepositoriesProductDbWaitTimeout
      got.repositories.order.get.dbWaitTimeout mustEqual expectRepositoriesOrderDbWaitTimeout

      got.modules.default.perPage mustEqual expectModulesDefaultPerPage
      got.modules.product.get.perPage mustEqual expectModulesProductPerPage
      got.modules.product.get.view mustEqual expectModulesProductView
    }

    "parse only required fields" in {
      val got = Config(readFile(path = requiredFieldsFilePath))

      got.app.settingsVersion mustEqual expectAppSettingsVersion
      got.repositories.default.dbWaitTimeout mustEqual expectRepositoriesDefaultDbWaitTimeout
      got.modules.default.perPage mustEqual expectModulesDefaultPerPage
    }

    "parse only required fields and call for defaults" in {
      val got = Config(readFile(path = requiredFieldsFilePath)).withDefaults()

      got.app.settingsVersion mustEqual expectAppSettingsVersion
      got.app.theme.get mustEqual Constants.App.theme
      got.app.settingsFilePath.get mustEqual Constants.App.settingsFilePath
      got.app.language.get mustEqual Constants.App.lang
      got.app.demoMode.get mustEqual Constants.App.demoMode
      got.app.startPage.get mustEqual Constants.App.startPage

      got.repositories.default.dbWaitTimeout mustEqual expectRepositoriesDefaultDbWaitTimeout
      got.repositories.delivery.get.dbWaitTimeout mustEqual expectRepositoriesDefaultDbWaitTimeout
      got.repositories.product.get.dbWaitTimeout mustEqual expectRepositoriesDefaultDbWaitTimeout

      got.modules.default.perPage mustEqual expectModulesDefaultPerPage
      got.modules.product.get.perPage mustEqual expectModulesDefaultPerPage
      got.modules.product.get.view mustEqual Constants.Modules.Product.view
    }

    "required field missing expect exception" in {
      val missingRequiredFields =
        """
          |app:
          |  theme: "goldfinch"
          |""".stripMargin

      a[DecodingFailure] mustBe thrownBy(Config.apply(missingRequiredFields))
    }
  }

  private def readFile(path: String): String = {
    val source = scala.io.Source.fromFile(path)

    val fileContents =
      try source.mkString
      finally source.close()

    fileContents
  }
}
