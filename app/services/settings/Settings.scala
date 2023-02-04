package services.settings

import java.io.{File, FileNotFoundException}
import java.nio.file.Paths

import scala.xml.{Elem, XML}

import play.api.Logging

import services.cache.Cache
import pkg.dto.{settings => dto}

case class Options(
                    systemSettingVersion: String,
                    readFilePath: String,
                    theme: String = "",
                    migrationsAppliedAt: String = "",
                    migrationsHash: String = "",
                    language: String = "",
                    startPage: String = "",
                    var writeFilePath: String = "", // default is loadFilePath
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
        contents = createAppSettingsFile(opts)
        currentVersion = dto.Settings(contents).version()
        logger.info("application settings created")
      case e: Throwable => throw e
    }

    logger.info(s"current application settings version: ${currentVersion}")

    if (currentVersion != opts.systemSettingVersion) {
      logger.info(
        s"local application settings version outdated. expect ${opts.systemSettingVersion} got $currentVersion",
      )
      logger.info("regenerating application settings...")
      contents = updateSystemSettings(fromContents = contents, fromVersion = currentVersion, defaults = opts)
      logger.info("complete")
    }

    save(dto.Settings(contents))
  }

  def settings(): dto.Settings = {
    cache.getOrCall[dto.Settings](config, () => dto.Settings(read()))
  }

  def version(): String = {
    settings().version()
  }

  def theme(): String = {
    settings().app().theme()
  }

  def appliedAt(): String = {
    settings().system().migrations().appliedAt()
  }

  def hash(): String = {
    settings().system().migrations().hash()
  }

  def language(): String = {
    settings().app().language()
  }

  def startPage(): String = {
    settings().app().startPage()
  }

  def updateTheme(theme: String): Unit = {
    update(theme = Option(theme))
  }

  def updateLanguage(language: String): Unit = {
    update(language = Option(language))
  }

  def updateStartPage(startPagePath: String): Unit = {
    update(startPage = Option(startPagePath))
  }

  def updateMigrationsInfo(appliedAt: String, hash: String): Unit = {
    update(appliedAt = Option(appliedAt), hash = Option(hash))
  }

  private def updateSystemSettings(fromContents: Elem, fromVersion: String, defaults: Options): Elem = {
    var contents: Elem = null

    val (success, updated) = update(
      currentVersion = fromVersion,
      targetVersion = defaults.systemSettingVersion,
      currentSettings = fromContents,
      defaults = defaults,
    )

    if (!success) {
      logger.error(
        s"failed to regenerate. settings versions v$fromVersion -> v${defaults.systemSettingVersion} are incompatible.",
      )
      logger.info("creating default settings file...")
      return createAppSettingsFile(defaults)
    }

    logger.info("settings updated")

    updated
  }

  private def createAppSettingsFile(opts: Options): xml.Elem = {
    val create = dto.Settings(
      version = opts.systemSettingVersion,
      theme = opts.theme,
      appliedAt = opts.migrationsAppliedAt,
      hash = opts.migrationsHash,
      language = opts.language,
      startPage = opts.startPage,
    )

    logger.info(s"settings file created version: ${opts.systemSettingVersion}")

    create.XML()
  }

  private def update(
                      version: Option[String] = None,
                      theme: Option[String] = None,
                      language: Option[String] = None,
                      startPage: Option[String] = None,
                      appliedAt: Option[String] = None,
                      hash: Option[String] = None,
                    ): Unit = {
    val update = dto.Settings(
      version = version,
      theme = theme,
      language = language,
      startPage = startPage,
      appliedAt = appliedAt,
      hash = hash,
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
