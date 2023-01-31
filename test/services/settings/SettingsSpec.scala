package services.settings

import org.scalatestplus.play.PlaySpec
import pkg.dto.{settings => dto}

class SettingsSpec extends PlaySpec {
  // values are equal stub file values
  val appliedAt: String = "2022-11-11 20:32:09.034"
  val hash: String = "8e48083b5b7638654cb5ec896884a5d28004c6b3"
  val language: String = "en"
  val startPage: String = "/products"
  val stubFilePath: String = "test/services/settings/stubs/settings.xml"

  "Settings" must {
    "load must throw no exception" in {
      noException must be thrownBy Settings.load(
        Options(
          systemSettingVersion = "0.0.4",
          settingsFilePath = stubFilePath,
          theme = "green",
          migrationsAppliedAt = appliedAt,
          migrationsHash = hash,
          language = language,
          startPage = startPage,
        ),
      )
    }

    "fill fields from given XML file" in {
      val expect = dto.Settings(
        dto.Version("0.0.4"),
        dto.App(
          dto.Theme("green"),
          dto.Language(language),
          dto.StartPage(startPage),
        ),
        dto.System(
          dto.Migrations(
            dto.AppliedAt(appliedAt),
            dto.Hash(hash),
          ),
        ),
      )

      Settings.load(
        Options(
          systemSettingVersion = "0.0.4",
          settingsFilePath = stubFilePath,
          theme = "green",
          migrationsAppliedAt = appliedAt,
          migrationsHash = hash,
          language = language,
          startPage = startPage,
        ),
      )
      val got = Settings.settings()

      got mustEqual expect
    }

    "return settings theme" in {
      val expect = "green"

      Settings.load(
        Options(
          systemSettingVersion = "0.0.4",
          settingsFilePath = stubFilePath,
          theme = "green",
          migrationsAppliedAt = appliedAt,
          migrationsHash = hash,
          language = language,
          startPage = startPage,
        ),
      )
      val got = Settings.theme()

      got mustEqual expect
    }

    "return settings appliedAt" in {
      val expect = appliedAt

      Settings.load(
        Options(
          systemSettingVersion = "0.0.4",
          settingsFilePath = stubFilePath,
          theme = "green",
          migrationsAppliedAt = appliedAt,
          migrationsHash = hash,
          language = language,
          startPage = startPage,
        ),
      )
      val got = Settings.appliedAt()

      got mustEqual expect
    }

    "return settings hash" in {
      val expect = hash

      Settings.load(
        Options(
          systemSettingVersion = "0.0.4",
          settingsFilePath = stubFilePath,
          theme = "green",
          migrationsAppliedAt = appliedAt,
          migrationsHash = hash,
          language = language,
          startPage = startPage,
        ),
      )
      val got = Settings.hash()

      got mustEqual expect
    }

    "return settings language" in {
      val expect = language

      Settings.load(
        Options(
          systemSettingVersion = "0.0.4",
          settingsFilePath = stubFilePath,
          theme = "green",
          migrationsAppliedAt = appliedAt,
          migrationsHash = hash,
          language = language,
          startPage = startPage,
        ),
      )
      val got = Settings.language()

      got mustEqual expect
    }

    "return settings startPage" in {
      val expect = startPage

      Settings.load(
        Options(
          systemSettingVersion = "0.0.4",
          settingsFilePath = stubFilePath,
          theme = "green",
          migrationsAppliedAt = appliedAt,
          migrationsHash = hash,
          language = language,
          startPage = startPage,
        ),
      )
      val got = Settings.startPage()

      got mustEqual expect
    }
  }
}
