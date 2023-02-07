package services.settings

import java.io.{File, FileNotFoundException}
import java.nio.file.Paths

import scala.xml.{Elem, XML}

import play.api.Logging

import services.cache.Cache
import pkg.dto.{settings => dto}

case class Options(
    appSettingsVersion: String,
    appTheme: String = "",
    appLanguage: String = "",
    appStartPage: String = "",
    systemMigrationsAppliedAt: String = "",
    systemMigrationsHash: String = "",
    moduleProductsPerPage: Int = 0,
    moduleProductsView: String = "",
    readFilePath: String,
    var writeFilePath: String = "",
)

object Settings extends Logging with CacheKeys with Updater {
  val cache: Cache = new Cache

  var options: Option[Options] = None

  def load(opts: Options): Unit = {
    options = Option(opts)

    val loaded = options.get

    if (loaded.writeFilePath.isEmpty) {
      loaded.writeFilePath = loaded.readFilePath
      options = Option(loaded)
    }

    var contents: xml.Elem = null
    var currentVersion: String = ""

    try {
      contents = read()
      currentVersion = dto.Settings(contents).version()
    } catch {
      case _: FileNotFoundException =>
        logger.info("application settings not found. creating new settings file...")
        contents = buildSettings(opts)
        currentVersion = dto.Settings(contents).version()
        logger.info("application settings created")
      case e: Throwable => throw e
    }

    logger.info(s"current application settings version: ${currentVersion}")

    if (currentVersion != opts.appSettingsVersion) {
      logger.info(
        s"local application settings version outdated. expect ${opts.appSettingsVersion} got $currentVersion",
      )
      logger.info("regenerating application settings...")
      contents = migrateSettingsVersion(fromContents = contents, fromVersion = currentVersion, defaults = opts)
      logger.info("complete")
    }

    save(dto.Settings(contents))
  }

  def settings(): dto.Settings = {
    cache.getOrCall[dto.Settings](config, () => dto.Settings(read()))
  }

  def appVersion(): String = {
    settings().version()
  }

  def appTheme(): String = {
    settings().app().theme()
  }

  def appLanguage(): String = {
    settings().app().language()
  }

  def appStartPage(): String = {
    settings().app().startPage()
  }

  def moduleProductsPerPage(): Int = {
    settings().modules().products().perPage().toInt
  }

  def moduleProductsView(): String = {
    settings().modules().products().view()
  }

  def systemMigrationsAppliedAt(): String = {
    settings().system().migrations().appliedAt()
  }

  def systemMigrationsHash(): String = {
    settings().system().migrations().hash()
  }

  def updateAppTheme(theme: String): Unit = {
    update(appTheme = Option(theme))
  }

  def updateAppLanguage(language: String): Unit = {
    update(appLanguage = Option(language))
  }

  def updateAppStartPage(startPagePath: String): Unit = {
    update(appStartPage = Option(startPagePath))
  }

  def updateAppMigrationsInfo(appliedAt: String, hash: String): Unit = {
    update(systemMigrationsAppliedAt = Option(appliedAt), systemMigrationsHash = Option(hash))
  }

  def updateModuleProductsPerPage(perPage: Int): Unit = {
    update(moduleProductsPerPage = Option(perPage))
  }

  def updateModuleProductsView(view: String): Unit = {
    update(moduleProductsView = Option(view))
  }

  private def migrateSettingsVersion(fromContents: Elem, fromVersion: String, defaults: Options): Elem = {
    val (success, updated) = update(
      currentVersion = fromVersion,
      targetVersion = defaults.appSettingsVersion,
      currentSettings = fromContents,
      defaults = defaults,
    )

    if (!success) {
      logger.error(
        s"failed to regenerate. settings versions v$fromVersion -> v${defaults.appSettingsVersion} are incompatible.",
      )
      logger.info("creating default settings file...")
      return buildSettings(defaults)
    }

    logger.info("settings updated")

    updated
  }

  private def buildSettings(opts: Options): xml.Elem = {
    val create = dto.Settings(
      appVersion = opts.appSettingsVersion,
      appLanguage = opts.appLanguage,
      appTheme = opts.appTheme,
      appStartPage = opts.appStartPage,
      systemMigrationsAppliedAt = opts.systemMigrationsAppliedAt,
      systemMigrationsHash = opts.systemMigrationsHash,
      moduleProductsPerPage = opts.moduleProductsPerPage,
      moduleProductsView = opts.moduleProductsView,
    )

    logger.info(s"settings file created version: ${opts.appSettingsVersion}")

    create.XML()
  }

  private def update(
      appVersion: Option[String] = None,
      appTheme: Option[String] = None,
      appLanguage: Option[String] = None,
      appStartPage: Option[String] = None,
      moduleProductsPerPage: Option[Int] = None,
      moduleProductsView: Option[String] = None,
      systemMigrationsAppliedAt: Option[String] = None,
      systemMigrationsHash: Option[String] = None,
  ): Unit = {
    val update = dto.Settings(
      appVersion = appVersion,
      appTheme = appTheme,
      appLanguage = appLanguage,
      appStartPage = appStartPage,
      moduleProductsPerPage = moduleProductsPerPage,
      moduleProductsView = moduleProductsView,
      systemMigrationsAppliedAt = systemMigrationsAppliedAt,
      systemMigrationsHash = systemMigrationsHash,
      base = settings(),
    )
    save(update)
  }

  private def save(settings: dto.Settings): Unit = {
    write(settings.XML())
    cache.update(config, settings)
    logger.info("settings saved")
  }

  private def write(contents: xml.Elem): Unit = {
    XML.save(filename = writeSettingsFile.toString, node = prettify(contents))
  }

  private def read(): xml.Elem = {
    try {
      XML.loadFile(readSettingsFile)
    } catch {
      case e: Throwable =>
        logger.error(s"read application settings exception: $e descr: ${e.toString}")
        throw e
    }
  }

  private def readSettingsFile: File = {
    Paths.get(options.get.readFilePath).toFile
  }

  private def writeSettingsFile: File = {
    Paths.get(options.get.writeFilePath).toFile
  }

  private def prettify(contents: xml.Elem): xml.Elem = {
    val printer = new scala.xml.PrettyPrinter(14, 4)
    XML.loadString(printer.format(contents))
  }
}

trait CacheKeys {
  val config: String = "config"
}
