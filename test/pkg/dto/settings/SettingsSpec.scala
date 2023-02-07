package pkg.dto.settings

import org.scalatestplus.play.PlaySpec
import scala.xml.XML
import pkg.dto.config.Constants

class SettingsSpec extends PlaySpec {
  val defaultFilePath: String = "test/stubs/settings.xml"
  val requiredFieldsFilePath: String = "test/pkg/dto/settings/stubs/read/required_settings.xml"

  // file values
  val version = "0.0.5"
  val theme: String = Constants.App.theme
  val language: String = Constants.App.lang
  val moduleProductsView: String = Constants.Modules.Product.view
  val startPage: String = Constants.App.startPage

  val appliedAt = "2022-11-11 20:32:09.034"
  val hash = "8e48083b5b7638654cb5ec896884a5d28004c6b3"
  val moduleProductsPerPage = 5

  "Settings dto XML" must {
    "load from stub file (all fields, default XML)" in {
      val expect = Settings(
        Version(version),
        App(
          theme = theme,
          language = language,
          startPage = startPage,
        ),
        Modules(
          Products(
            perPage = moduleProductsPerPage.toString,
            view = moduleProductsView,
          ),
        ),
        System(
          Migrations(
            appliedAt = appliedAt,
            hash = hash,
          ),
        ),
      )

      val got = Settings(XML.loadFile(defaultFilePath))

      got mustEqual expect
    }

    "load from stub file (only required fields, required XML)" in {
      val expect = Settings(
        Version(version),
        System(
          Migrations(
            appliedAt = appliedAt,
            hash = hash,
          ),
        ),
      )

      val got = Settings(XML.loadFile(requiredFieldsFilePath))

      got.version() mustEqual expect.version()
      got.system().migrations().appliedAt() mustEqual expect.system().migrations().appliedAt()
      got.system().migrations().hash() mustEqual expect.system().migrations().hash()
    }
  }
}
