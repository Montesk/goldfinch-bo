package services.settings

import scala.xml.XML
import org.scalatestplus.play.PlaySpec

class SettingsSpec extends PlaySpec {
  // values are equal stub file values
  val version: String = "0.0.4"
  val theme: String = "green"
  val appliedAt: String = "2022-11-11 20:32:09.034"
  val hash: String = "8e48083b5b7638654cb5ec896884a5d28004c6b3"
  val language: String = "en"
  val startPage: String = "/products"
  val defaultFilePath: String = "test/stubs/settings.xml"
  val stubFilePath: String = "test/services/settings/stubs/settings.xml"

  "Settings" must {
    "load throw no exception" in {
      try {
        noException must be thrownBy Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = stubFilePath,
            theme = theme,
            migrationsAppliedAt = appliedAt,
            migrationsHash = hash,
            language = language,
            startPage = startPage,
          ),
        )
      } finally {
        testTearDown()
      }
    }
    "load all given from options fields" in {
      try {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = stubFilePath,
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
      } finally {
        testTearDown()
      }
    }
    "update theme" in {
      val expect = "yellow"

      try {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = stubFilePath,
          ),
        )

        Settings.updateTheme(expect)
        Settings.theme() mustEqual expect

      } finally {
        testTearDown()
      }
    }
    "update migrationsInfo" in {
      val expectAppliedAt = "2022-12-12 20:32:09.034"
      val expectHash = "new_hash"

      try {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = stubFilePath,
          ),
        )

        Settings.updateMigrationsInfo(appliedAt = expectAppliedAt, hash = expectHash)
        Settings.appliedAt() mustEqual expectAppliedAt
        Settings.hash() mustEqual expectHash

      } finally {
        testTearDown()
      }
    }
    "update language" in {
      val expect = "nl"

      try {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = stubFilePath,
          ),
        )

        Settings.updateLanguage(expect)
        Settings.language() mustEqual expect

      } finally {
        testTearDown()
      }
    }
    "update start page" in {
      val expect = "/deliveries"

      try {
        Settings.load(
          Options(
            systemSettingVersion = version,
            readFilePath = stubFilePath,
          ),
        )

        Settings.updateStartPage(expect)
        Settings.startPage() mustEqual expect

      } finally {
        testTearDown()
      }
    }
    "return settings theme" in {
      val expect = "goldfinch"

      try {
        Settings.load(
          Options(
            systemSettingVersion = "0.0.4",
            readFilePath = "",
            writeFilePath = stubFilePath,
            theme = expect,
          ),
        )

        val got = Settings.theme()
        got mustEqual expect

      } finally {
        testTearDown()
      }
    }
    "return settings appliedAt" in {
      val expect = "2023-11-11 20:32:09.034"

      try {
        Settings.load(
          Options(
            systemSettingVersion = "0.0.4",
            readFilePath = "",
            writeFilePath = stubFilePath,
            migrationsAppliedAt = expect,
          ),
        )

        val got = Settings.appliedAt()
        got mustEqual expect

      } finally {
        testTearDown()
      }
    }
    "return settings hash" in {
      val expect = "stub_hash"

      try {
        Settings.load(
          Options(
            systemSettingVersion = "0.0.4",
            readFilePath = "",
            writeFilePath = stubFilePath,
            migrationsHash = expect,
          ),
        )

        val got = Settings.hash()
        got mustEqual expect

      } finally {
        testTearDown()
      }
    }
    "return settings language" in {
      val expect = "gb"

      try {
        Settings.load(
          Options(
            systemSettingVersion = "0.0.4",
            readFilePath = "",
            writeFilePath = stubFilePath,
            language = expect,
          ),
        )
        val got = Settings.language()
        got mustEqual expect

      } finally {
        testTearDown()
      }
    }
    "return settings startPage" in {
      val expect = "/warehouse"

      try {
        Settings.load(
          Options(
            systemSettingVersion = "0.0.4",
            readFilePath = "",
            writeFilePath = stubFilePath,
            startPage = expect,
          ),
        )

        val got = Settings.startPage()
        got mustEqual expect

      } finally {
        testTearDown()
      }
    }
    "return settings version" in {
      val expect = "0.0.4"

      try {
        Settings.load(
          Options(
            systemSettingVersion = expect,
            readFilePath = "",
            writeFilePath = stubFilePath,
          ),
        )

        val got = Settings.version()
        got mustEqual expect

      } finally {
        testTearDown()
      }
    }
  }

  private def testTearDown(): Unit = {
    XML.save(stubFilePath, XML.loadFile(defaultFilePath))
  }
}
