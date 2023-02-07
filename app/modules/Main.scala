package modules

import com.google.inject.AbstractModule

import play.api.Logging

import services.config.Config
import services.migrations.{Migrations, DefaultConfig => MigrationsConfig}
import services.settings.{Settings, Options => FromConfig}

/**
 * This class is a Guice module that tells Guice how to bind several different types. This Guice module is created when
 * the Play application starts. Play will automatically use any class called `Module` that is in the root package. You
 * can create modules in other locations by adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Main extends AbstractModule with Logging {
  override def configure(): Unit = {
    logger.info("Loading application configuration")
    val source = scala.io.Source.fromFile(AppSettings.ConfigPath)

    val fileContents =
      try source.mkString
      finally source.close()

    Config.load(fileContents)
    logger.info("Application config loaded")

    val migrationService = new Migrations(MigrationsConfig())
    val lastApplied = migrationService.lastApplied()

    logger.info("Loading application profile settings")
    Settings.load(
      FromConfig(
        appSettingsVersion = Config.appSettingsVersion(),
        appTheme = Config.appTheme(),
        appLanguage = Config.appLanguage(),
        appStartPage = Config.appStartPage(),
        systemMigrationsAppliedAt = lastApplied.appliedAt,
        systemMigrationsHash = lastApplied.hash,
        moduleProductsPerPage = Config.modulesProductPerPage(),
        moduleProductsView = Config.modulesProductView(),
        readFilePath = Config.appSettingsFilePath(),
      ),
    )
    logger.info("Application profile settings loaded")

    if (
      Settings.systemMigrationsHash() != lastApplied.hash || Settings
        .systemMigrationsAppliedAt() != lastApplied.appliedAt
    ) {
      logger.warn("Generated migration info is outdated")
      logger.info("Generating migrations info...")
      migrationService.writeInfo()
      logger.info("Application migrations info generated")

      Settings.updateAppMigrationsInfo(appliedAt = lastApplied.appliedAt, hash = lastApplied.hash)
    }

    logger.info("Application started")
  }

  protected object AppSettings {
    val ConfigPath: String = "conf/config.yml"
  }
}
