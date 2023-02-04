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

object Settings extends Logging with CacheKeys {
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

    try {
      contents = read()
    } catch {
      case _: FileNotFoundException =>
        logger.info("application settings not found. creating new settings file...")
        contents = createAppSettingsFile(opts)
        logger.info("application settings created")
      case e: Throwable => throw e
    }

    var currentVersion: String = ""
    try {
      currentVersion = dto.Settings(contents).version()
    } catch {
      case e: Throwable => {
        logger.error(s"failed to retrieve current settings version. exception: ${e}")
        logger.info("create application settings...")
        contents = createAppSettingsFile(opts)
        currentVersion = dto.Settings(contents).version()
        logger.info("complete")
      }
    }

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
    val prev = settings()

    val update = dto.Settings(
      dto.Version(prev.version()),
      dto.App(
        theme = theme,
        language = language(),
        startPage = startPage(),
      ),
      prev.system(),
    )

    save(update)
  }

  def updateLanguage(language: String): Unit = {
    val prev = settings()

    val update = dto.Settings(
      dto.Version(prev.version()),
      dto.App(
        theme = theme(),
        language = language,
        startPage = startPage(),
      ),
      prev.system(),
    )

    save(update)
  }

  def updateMigrationsInfo(appliedAt: String, hash: String): Unit = {
    val prev = settings()

    val update = dto.Settings(
      dto.Version(prev.version()),
      prev.app(),
      dto.System(
        dto.Migrations(
          appliedAt = appliedAt,
          hash = hash,
        ),
      ),
    )

    save(update)
  }

  def updateStartPage(startPagePath: String): Unit = {
    val prev = settings()

    val update = dto.Settings(
      dto.Version(prev.version()),
      dto.App(
        theme = theme(),
        language = language(),
        startPage = startPagePath,
      ),
      prev.system(),
    )

    save(update)
  }

  private def createAppSettingsFile(opts: Options): xml.Elem = {
    val create = settings(
      version = opts.systemSettingVersion,
      theme = opts.theme,
      appliedAt = opts.migrationsAppliedAt,
      hash = opts.migrationsHash,
      language = opts.language,
      startPage = opts.startPage,
    )

    create.XML()
  }

  private def settings(
      version: String,
      theme: String,
      appliedAt: String,
      hash: String,
      language: String,
      startPage: String,
  ): dto.Settings = {
    dto.Settings(
      dto.Version(version),
      dto.App(
        theme = theme,
        language = language,
        startPage = startPage,
      ),
      dto.System(
        dto.Migrations(
          appliedAt = appliedAt,
          hash = hash,
        ),
      ),
    )
  }

  private def updateSystemSettings(fromContents: Elem, fromVersion: String, defaults: Options): Elem = {
    var contents: Elem = null
    try {
      logger.info("trying to regenerate from old version")
      contents = createAppSettingsFile(
        optionsFromContent(fromContents, defaults.readFilePath, defaults.systemSettingVersion),
      )
    } catch {
      case e: Throwable => {
        logger.error(
          s"failed to regenerate. settings versions v$fromVersion -> v${defaults.systemSettingVersion} are incompatible. exception ${e}",
        )
        logger.info("creating default settings file")
        contents = createAppSettingsFile(defaults)
      }
    }
    logger.info(s"created \r\n ${prettify(contents)}")
    contents
  }

  // Trying to restore some options from previous version of settings XML
  // :NOTICE:
  //  settings DTO and previous version of xml must be compatible (no exception)
  //  its up to developer to check if settings is no more compatible or
  //  taking into consideration that some fields will be NOT marshalled
  private def optionsFromContent(oldContents: Elem, filePath: String, toVersion: String): Options = {
    val restored = dto.Settings(oldContents)

    Options(
      systemSettingVersion = toVersion,
      readFilePath = filePath,
      theme = restored.app().theme(),
      migrationsAppliedAt = restored.system().migrations().appliedAt(),
      migrationsHash = restored.system().migrations().hash(),
      language = restored.app().language(),
      startPage = restored.app().startPage(),
    )
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
