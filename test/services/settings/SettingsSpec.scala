package services.settings

import scala.xml.XML
import org.scalatestplus.play.PlaySpec
import org.xml.sax.SAXParseException

class SettingsSpec extends PlaySpec {
  // values are equal stub file values
  val version: String = "0.0.4"
  val theme: String = "green"
  val appliedAt: String = "2022-11-11 20:32:09.034"
  val hash: String = "8e48083b5b7638654cb5ec896884a5d28004c6b3"
  val language: String = "en"
  val startPage: String = "/products"
  val readFilePath: String = "test/stubs/settings.xml"
  val writeFilePath: String = "test/services/settings/stubs/write/settings.xml"
  val invalidSettingsFilePath: String = "test/services/settings/stubs/read/invalid_settings.xml"

  // update stub files values (differs from default)
  val updateLanguage: String = "nl"
  val updateStartPage: String = "/warehouse"
  val updateTheme: String = "goldfinch"
  val updateAppliedAt: String = "2023-11-11 20:32:09.034"
  val updateHash: String = "8e48083b5b7638654cb5ec896884a5d28004c6b4"

  def readFilePath(version: String): String = {
    s"test/services/settings/stubs/read/settings_v${version}.xml"
  }

  "Settings" must {
    "load throw no exception (all values defaults)" in {
      runTest(() => {
        noException must be thrownBy Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = readFilePath,
            theme = theme,
            migrationsAppliedAt = appliedAt,
            migrationsHash = hash,
            language = language,
            startPage = startPage,
          ),
        )
      })
    }
    "load throws exception (invalid file)" in {
      runTest(() => {
        a[SAXParseException] must be thrownBy Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = invalidSettingsFilePath,
            writeFilePath = writeFilePath,
            theme = theme,
            migrationsAppliedAt = appliedAt,
            migrationsHash = hash,
            language = language,
            startPage = startPage,
          ),
        )
      })
    }
    "load throw no exception (all values defaults, file not found)" in {
      runTest(() => {
        noException must be thrownBy Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = "",
            writeFilePath = writeFilePath,
            theme = theme,
            migrationsAppliedAt = appliedAt,
            migrationsHash = hash,
            language = language,
            startPage = startPage,
          ),
        )
      })
    }
    "load all given from options fields" in {
      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
            theme = theme,
            migrationsAppliedAt = appliedAt,
            migrationsHash = hash,
            language = language,
            startPage = startPage,
          ),
        )

        Settings.version() mustEqual version
        Settings.theme() mustEqual theme
        Settings.appliedAt() mustEqual appliedAt
        Settings.hash() mustEqual hash
        Settings.language() mustEqual language
        Settings.startPage() mustEqual startPage
      })
    }
    "update theme" in {
      val expect = "yellow"

      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.updateTheme(expect)
        Settings.theme() mustEqual expect
      })
    }
    "update migrationsInfo" in {
      val expectAppliedAt = "2022-12-12 20:32:09.034"
      val expectHash = "new_hash"

      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.updateMigrationsInfo(appliedAt = expectAppliedAt, hash = expectHash)
        Settings.appliedAt() mustEqual expectAppliedAt
        Settings.hash() mustEqual expectHash
      })
    }
    "update language" in {
      val expect = "nl"

      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.updateLanguage(expect)
        Settings.language() mustEqual expect
      })
    }
    "update start page" in {
      val expect = "/deliveries"

      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.updateStartPage(expect)
        Settings.startPage() mustEqual expect
      })
    }
    "return settings theme" in {
      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = "0.0.4",
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.theme() mustEqual theme
      })
    }
    "return settings appliedAt" in {
      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = "0.0.4",
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.appliedAt() mustEqual appliedAt
      })
    }
    "return settings hash" in {
      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = "0.0.4",
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.hash() mustEqual hash
      })
    }
    "return settings language" in {
      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = "0.0.4",
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.language() mustEqual language
      })
    }
    "return settings startPage" in {
      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = "0.0.4",
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.startPage() mustEqual startPage
      })
    }
    "return settings version" in {
      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
          ),
        )

        Settings.version() mustEqual version
      })
    }
    "update settings (single update)" in {
      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = readFilePath("0.0.3"),
            writeFilePath = writeFilePath,
            startPage = startPage,
          ),
        )

        Settings.version() mustEqual version
        Settings.theme() mustEqual updateTheme
        Settings.appliedAt() mustEqual updateAppliedAt
        Settings.hash() mustEqual updateHash
        Settings.language() mustEqual updateLanguage
        Settings.startPage() mustEqual startPage
      })
    }
    "update settings (sequence of updates)" in {
      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = readFilePath("0.0.2"),
            writeFilePath = writeFilePath,
            language = language,
            startPage = startPage,
          ),
        )

        Settings.version() mustEqual version
        Settings.theme() mustEqual updateTheme
        Settings.appliedAt() mustEqual updateAppliedAt
        Settings.hash() mustEqual updateHash
        Settings.language() mustEqual language
        Settings.startPage() mustEqual startPage
      })
    }
    "update settings (no update required)" in {
      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = readFilePath("0.0.4"),
            writeFilePath = writeFilePath,
          ),
        )

        Settings.version() mustEqual version
        Settings.theme() mustEqual updateTheme
        Settings.appliedAt() mustEqual updateAppliedAt
        Settings.hash() mustEqual updateHash
        Settings.language() mustEqual updateLanguage
        Settings.startPage() mustEqual updateStartPage
      })
    }
    "update settings (no update found, generate default options)" in {
      runTest(() => {
        Settings.load(
          Options(
            systemSettingVersion = "2.0.4",
            readFilePath = readFilePath,
            writeFilePath = writeFilePath,
            theme = theme,
            migrationsAppliedAt = appliedAt,
            migrationsHash = hash,
            language = language,
            startPage = startPage,
          ),
        )

        Settings.version() mustEqual "2.0.4"
        Settings.theme() mustEqual theme
        Settings.appliedAt() mustEqual appliedAt
        Settings.hash() mustEqual hash
        Settings.language() mustEqual language
        Settings.startPage() mustEqual startPage
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
