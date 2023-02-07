package services.settings

import scala.xml.XML

import org.scalatestplus.play.PlaySpec
import org.xml.sax.SAXParseException

import pkg.dto.config.Constants

class SettingsSpec extends PlaySpec {
  // values are equal stub file values and defaults from constants
  val appVersion: String = "0.0.5"
  val appTheme: String = Constants.App.theme
  val appLanguage: String = Constants.App.lang
  val appStartPage: String = Constants.App.startPage
  val demoMode: Boolean = Constants.App.demoMode
  val moduleProductsView: String = Constants.Modules.Product.view
  val moduleProductsPerPage: Int = 5
  val systemMigrationsAppliedAt: String = "2022-11-11 20:32:09.034"
  val systemMigrationsHash: String = "8e48083b5b7638654cb5ec896884a5d28004c6b3"

  val readFilePath: String = "test/stubs/settings.xml"
  val writeFilePath: String = "test/services/settings/stubs/write/settings.xml"
  val invalidSettingsFilePath: String = "test/services/settings/stubs/read/invalid_settings.xml"

  // update stub files values (differs from default)
  val existingTheme: String = "goldfinch"
  val existingSystemMigrationsAppliedAt: String = "2023-11-11 20:32:09.034"
  val existingSystemMigrationsHash: String = "8e48083b5b7638654cb5ec896884a5d28004c6b4"

  def readFilePath(version: String): String = {
    s"test/services/settings/stubs/read/settings_v${version}.xml"
  }

  "Settings" must {
    "load throw no exception (all values defaults)" in {
      runTest(() => {
        noException must be thrownBy Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )
      })
    }
    "load throws exception (invalid file)" in {
      runTest(() => {
        a[SAXParseException] must be thrownBy Settings.load(
          Options(
            appSettingsVersion = "",
            readFilePath = invalidSettingsFilePath,
            writeFilePath = writeFilePath,
          ),
        )
      })
    }
    "load throw no exception (file not found)" in {
      runTest(() => {
        noException must be thrownBy Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = "",
            writeFilePath = writeFilePath,
          ),
        )
      })
    }
    "load all given from options fields" in {
      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            appTheme = appTheme,
            appLanguage = appLanguage,
            appStartPage = appStartPage,
            systemMigrationsAppliedAt = systemMigrationsAppliedAt,
            systemMigrationsHash = systemMigrationsHash,
            moduleProductsPerPage = moduleProductsPerPage,
            moduleProductsView = moduleProductsView,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.appVersion() mustEqual appVersion
        Settings.appTheme() mustEqual appTheme
        Settings.appLanguage() mustEqual appLanguage
        Settings.appStartPage() mustEqual appStartPage
        Settings.moduleProductsPerPage() mustEqual moduleProductsPerPage
        Settings.moduleProductsView() mustEqual moduleProductsView
        Settings.systemMigrationsAppliedAt() mustEqual systemMigrationsAppliedAt
        Settings.systemMigrationsHash() mustEqual systemMigrationsHash
      })
    }
    "update theme" in {
      val expect = "yellow"

      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.updateAppTheme(expect)
        Settings.appTheme() mustEqual expect
      })
    }
    "update migrationsInfo" in {
      val expectAppliedAt = "2022-12-12 20:32:09.034"
      val expectHash = "new_hash"

      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.updateAppMigrationsInfo(appliedAt = expectAppliedAt, hash = expectHash)
        Settings.systemMigrationsAppliedAt() mustEqual expectAppliedAt
        Settings.systemMigrationsHash() mustEqual expectHash
      })
    }
    "update language" in {
      val expect = "nl"

      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.updateAppLanguage(expect)
        Settings.appLanguage() mustEqual expect
      })
    }
    "update start page" in {
      val expect = "/deliveries"

      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.updateAppStartPage(expect)
        Settings.appStartPage() mustEqual expect
      })
    }
    "update products module perPage" in {
      val expect = 12

      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.updateModuleProductsPerPage(expect)
        Settings.moduleProductsPerPage() mustEqual expect
      })
    }
    "update products module view" in {
      val expect = "album"

      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.updateModuleProductsView(expect)
        Settings.moduleProductsView() mustEqual expect
      })
    }
    "return settings theme" in {
      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.appTheme() mustEqual appTheme
      })
    }
    "return settings appliedAt" in {
      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.systemMigrationsAppliedAt() mustEqual systemMigrationsAppliedAt
      })
    }
    "return settings hash" in {
      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.systemMigrationsHash() mustEqual systemMigrationsHash
      })
    }
    "return settings language" in {
      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.appLanguage() mustEqual appLanguage
      })
    }
    "return settings startPage" in {
      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.appStartPage() mustEqual appStartPage
      })
    }
    "return settings version" in {
      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.appVersion() mustEqual appVersion
      })
    }
    "return module products perPage" in {
      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            moduleProductsPerPage = moduleProductsPerPage,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.moduleProductsPerPage() mustEqual moduleProductsPerPage
      })
    }
    "return module products view" in {
      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            moduleProductsPerPage = moduleProductsPerPage,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.moduleProductsPerPage() mustEqual moduleProductsPerPage
      })
    }
    "update settings (sequence of updates)" in {
      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            appLanguage = appLanguage,
            appStartPage = appStartPage,
            moduleProductsPerPage = moduleProductsPerPage,
            moduleProductsView = moduleProductsView,
            readFilePath = readFilePath("0.0.2"),
            writeFilePath = writeFilePath,
          ),
        )

        Settings.appVersion() mustEqual appVersion
        Settings.appTheme() mustEqual existingTheme
        Settings.systemMigrationsAppliedAt() mustEqual existingSystemMigrationsAppliedAt
        Settings.systemMigrationsHash() mustEqual existingSystemMigrationsHash
        // values below will be created via update
        Settings.appLanguage() mustEqual appLanguage
        Settings.appStartPage() mustEqual appStartPage
        Settings.moduleProductsPerPage() mustEqual moduleProductsPerPage
        Settings.moduleProductsView() mustEqual moduleProductsView
      })
    }
    "update settings (no update required)" in {
      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = appVersion,
            readFilePath = readFilePath("0.0.5"),
            writeFilePath = writeFilePath,
          ),
        )

        Settings.appVersion() mustEqual appVersion
      })
    }
    "update settings (no update found, generate default options)" in {
      runTest(() => {
        Settings.load(
          Options(
            appSettingsVersion = "2.0.4",
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.appVersion() mustEqual "2.0.4"
      })
    }
  }

  private def runTest(testCase: () => {}): Unit = {
    try {
      testCase()
    } finally {
      testTearDown()
    }
  }

  private def testTearDown(): Unit = {
    XML.save(writeFilePath, XML.loadFile(readFilePath))
  }
}
