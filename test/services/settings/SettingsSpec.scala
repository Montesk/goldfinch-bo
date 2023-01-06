package services.settings

import org.scalatestplus.play.PlaySpec
import pkg.dto.{settings => dto}

class SettingsSpec extends PlaySpec {
  val appliedAt: String = "2022-11-11 20:32:09.034" // same as file
  val hash: String = "8e48083b5b7638654cb5ec896884a5d28004c6b3" // same as file
  val stubFilePath: String = "test/services/settings/stubs/settings.xml"

  "Settings" must {
    "load must throw no exception" in {
      noException must be thrownBy Settings.load(
        Options(
          systemSettingVersion = "0.0.2",
          settingsFilePath = stubFilePath,
          theme = "green",
          migrationsAppliedAt = appliedAt,
          migrationsHash = hash
        )
      )
    }

    "must provide must fill fields from given XML file" in {
      val expect = dto.Settings(
        dto.Version("0.0.2"),
        dto.App(
          dto.Theme("green")
        ),
        dto.System(
          dto.Migrations(
            dto.AppliedAt(appliedAt),
            dto.Hash(hash)
          )
        )
      )

      Settings.load(
        Options(
          systemSettingVersion = "0.0.2",
          settingsFilePath = stubFilePath,
          theme = "green",
          migrationsAppliedAt = appliedAt,
          migrationsHash = hash
        )
      )
      val got = Settings.settings()

      got mustEqual expect
    }

    "must return settings theme" in {
      val expect = "green"

      Settings.load(
        Options(
          systemSettingVersion = "0.0.2",
          settingsFilePath = stubFilePath,
          theme = "green",
          migrationsAppliedAt = appliedAt,
          migrationsHash = hash
        )
      )
      val got = Settings.theme()

      got mustEqual expect
    }

    "must return settings appliedAt" in {
      val expect = appliedAt

      Settings.load(
        Options(
          systemSettingVersion = "0.0.2",
          settingsFilePath = stubFilePath,
          theme = "green",
          migrationsAppliedAt = appliedAt,
          migrationsHash = hash
        )
      )
      val got = Settings.appliedAt()

      got mustEqual expect
    }

    "must return settings hash" in {
      val expect = hash

      Settings.load(
        Options(
          systemSettingVersion = "0.0.2",
          settingsFilePath = stubFilePath,
          theme = "green",
          migrationsAppliedAt = appliedAt,
          migrationsHash = hash
        )
      )
      val got = Settings.hash()

      got mustEqual expect
    }
  }
}
