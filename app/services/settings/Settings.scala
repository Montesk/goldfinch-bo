package services.settings

import java.io.FileNotFoundException

import scala.xml.XML

import play.api.Logging

import services.cache.Cache
import services.config.AppSettings

case class ApplicationConfig(theme: String)

case class MigrationsGenerateInfo(appliedAt: String, hash: String)

trait Settings {
  def config(): ApplicationConfig
}

object Settings extends Logging with CacheKeys with SettingKeys {
  val cache: Cache = new Cache

  private val version = "0.0.2"
  private var filePath: String = ""

  def config(): ApplicationConfig = {
    cache.get(
      configCacheKey,
      ApplicationConfig(theme = readTag(cache.getOrCall(fileContentsCacheKey, read), themeCacheKey))
    )
  }

  def migrationsInfoGenerated(): MigrationsGenerateInfo = {
    val contents = cache.getOrCall(fileContentsCacheKey, read)
    MigrationsGenerateInfo(
      appliedAt = readTag(contents, appliedAtSettingsKey),
      hash = readTag(contents, hashAtSettingsKey)
    )
  }

  def load(config: AppSettings, appliedAt: String, hash: String): Unit = {
    filePath = config.settingsFilePath
    var contents: xml.Elem = null

    try {
      contents = XML.loadFile(filePath)
    } catch {
      case _: FileNotFoundException =>
        logger.info("application settings not found. creating new settings file...")
        contents = createAppSettingsFile(appConfig = config, appliedAt = appliedAt, hash = hash)
        logger.info("application settings created")
      case e: Throwable => throw e
    }

    val settingsVersion = readTag(contents, versionSettingsKey)

    if (settingsVersion != version) {
      logger.info(s"local application settings version outdated. expect $version got $settingsVersion")
      logger.info("regenerating application settings...")
      contents = createAppSettingsFile(appConfig = config, appliedAt = appliedAt, hash = hash)
      logger.info(s"complete $contents")
    }
  }

  def updateTheme(theme: String): Unit = {
    val contents = cache.getOrCall(fileContentsCacheKey, read)
    val settingsXML = xmlContents(
      version = readTag(contents, versionSettingsKey),
      theme = theme,
      appliedAt = readTag(contents, tagName = appliedAtSettingsKey),
      hash = readTag(contents, tagName = hashAtSettingsKey)
    )
    write(settingsXML)
    cache.update(configCacheKey, ApplicationConfig(theme = theme))
  }

  def updateMigrationsGenerateInfo(info: MigrationsGenerateInfo): Unit = {
    val contents = cache.getOrCall(fileContentsCacheKey, read)
    val settingsXML = xmlContents(
      version = readTag(contents, versionSettingsKey),
      theme = readTag(contents, themeSettingsKey),
      appliedAt = info.appliedAt,
      hash = info.hash
    )
    write(settingsXML)
  }

  def update[T](key: String, value: T): Unit = {
    key match {
      case `themeSettingsKey`     => updateTheme(value.asInstanceOf[String])
      case `migrationSettingsKey` => updateMigrationsGenerateInfo(value.asInstanceOf[MigrationsGenerateInfo])
      case _                      => logger.warn(s"unknown app setting $key")
    }
  }

  private def createAppSettingsFile(appConfig: AppSettings, appliedAt: String, hash: String): xml.Elem = {
    val settingsXML = xmlContents(version = version, theme = appConfig.theme, appliedAt = appliedAt, hash = hash)
    logger.info(s"init application settings \n ${prettify(settingsXML)}")
    write(settingsXML)
    settingsXML
  }

  private def xmlContents(version: String, theme: String, appliedAt: String, hash: String): xml.Elem = {
    <settings>
      <version>
        {version}
      </version>
      <app>
        <theme>
          {theme}
        </theme>
      </app>
      <system>
        <migrations>
          <appliedAt>
            {appliedAt}
          </appliedAt>
          <hash>
            {hash}
          </hash>
        </migrations>
      </system>
    </settings>
  }

  private def read(): xml.Elem = {
    try {
      XML.loadFile(filePath)
    } catch {
      case e: Throwable =>
        logger.error(s"read application settings exception: $e descr: ${e.toString}")
        <root></root>

    }
  }

  private def write(contents: xml.Elem): Unit = {
    XML.save(filename = filePath, node = prettify(contents))
    cache.update(fileContentsCacheKey, contents)
  }

  private def prettify(contents: xml.Elem): xml.Elem = {
    val printer = new scala.xml.PrettyPrinter(14, 4)
    XML.loadString(printer.format(contents))
  }

  private def readTag(contents: xml.Elem, tagName: String): String = {
    val res = contents \\ tagName
    if (res.isEmpty) {
      logger.error(s"tagName: $tagName not found in XML: $contents")
      return ""
    }
    res.head.text.trim
  }
}

trait SettingKeys {
  // top-level
  val versionSettingsKey: String = "version"
  // app
  val themeSettingsKey: String = "theme"
  // system
  val migrationSettingsKey: String = "migrations"
  //// migrations
  val appliedAtSettingsKey: String = "appliedAt"
  val hashAtSettingsKey: String = "hash"
}

trait CacheKeys {
  val themeCacheKey: String = "theme"
  val fileContentsCacheKey: String = "settings"
  val configCacheKey: String = "config"
}
