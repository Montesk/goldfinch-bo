package pkg.dto.settings

import org.scalatestplus.play.PlaySpec

import scala.xml.XML

class SettingsSpec extends PlaySpec {
  val defaultFilePath: String = "test/stubs/settings.xml"
  val requiredFieldsFilePath: String = "test/stubs/required_settings.xml"

  // file values
  val version = "0.0.4"
  val theme = "green"
  val language = "en"
  val startPage = "/products"
  val appliedAt = "2022-11-11 20:32:09.034"
  val hash = "8e48083b5b7638654cb5ec896884a5d28004c6b3"

  "Settings dto XML" must {
    "load from stub file (all fields, default XML)" in {
      val expect = Settings(
        Version(version),
        App(
          theme = theme,
          language = language,
          startPage = startPage,
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
