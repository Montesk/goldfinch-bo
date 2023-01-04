package services.settings

import java.io.FileNotFoundException
import scala.xml.XML

import play.api.Logging

import services.cache.Cache
import services.config.AppSettings

case class ApplicationConfig(theme: String)

trait Settings {
  def config(): ApplicationConfig
}

object Settings extends Logging with SettingKeys with CacheKeys {
  val cache: Cache = new Cache

  private val version = "0.0.1"
  private var filePath: String = ""

  def config(): ApplicationConfig = {
    cache.get(
      "config",
      ApplicationConfig(theme = readTag(cache.getOrCall(fileContentsCacheKey, read), themeCacheKey))
    )
  }

  def load(config: AppSettings): Unit = {
    filePath = config.settingsFilePath
    var contents: xml.Elem = null

    try {
      contents = XML.loadFile(filePath)
    } catch {
      case _: FileNotFoundException => {
        logger.info("application settings not found. creating new settings file...")
        contents = createAppSettingsFile(config)
        logger.info("application settings created")
      }
      case e: Throwable => throw e
    }
  }

  def updateTheme(theme: String): Unit = {
    val contents = cache.getOrCall(fileContentsCacheKey, read)
    val settingsXML = xmlContents(
      {
        readTag(contents, "version")
      },
      theme
    )
    write(settingsXML)
    cache.update("config", ApplicationConfig(theme = theme))
  }

  def update(key: String, value: String): Unit = {
    key match {
      case `themeSettingKey` => updateTheme(value)
      case _                 => logger.warn(s"unknown app setting $key")
    }
  }

  private def createAppSettingsFile(appConfig: AppSettings): xml.Elem = {
    val settingsXML = xmlContents(version, appConfig.theme)
    logger.info(s"init application settings \n ${prettify(settingsXML)}")
    write(settingsXML)
    settingsXML
  }

  private def xmlContents(version: String, theme: String): xml.Elem = {
    <settings>
      <version>
        {version}
      </version>
      <app>
        <theme>
          {theme}
        </theme>
      </app>
    </settings>
  }

  private def read(): xml.Elem = {
    XML.loadFile(filePath)
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
    res.head.text.trim
  }
}

trait SettingKeys {
  val themeSettingKey: String = "theme"
}

trait CacheKeys extends SettingKeys {
  val themeCacheKey: String = themeSettingKey
  val fileContentsCacheKey: String = "settings"
}
