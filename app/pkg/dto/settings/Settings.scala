package pkg.dto.settings

import scala.xml.{Elem, NodeSeq}
import pkg.xml.Serializable

object Settings {
  def apply(xml: Elem): Settings = {
    new Settings(
      Version(requiredTag(xml \ "version")),
      App(
        Theme(optionalTag(xml \ "app" \ "theme")),
        Language(optionalTag(xml \ "app" \ "language")),
        StartPage(optionalTag(xml \ "app" \ "startPage")),
      ),
      System(
        Migrations(
          AppliedAt(requiredTag(xml \ "system" \ "migrations" \ "appliedAt")),
          Hash(requiredTag(xml \ "system" \ "migrations" \ "hash")),
        ),
      ),
    )
  }

  def apply(values: Serializable*): Settings = {
    new Settings(values: _*)
  }

  /**
   * Values constructor helper preserves standard settings xml form (field orders)
   * @return
   */
  def apply(
      version: String,
      theme: String,
      language: String,
      startPage: String,
      appliedAt: String,
      hash: String,
  ): Settings = {
    new Settings(
      Version(version),
      App(
        theme = theme,
        language = language,
        startPage = startPage,
      ),
      System(
        Migrations(
          appliedAt = appliedAt,
          hash = hash,
        ),
      ),
    )
  }

  /**
   * Helper constructor with update values and fallback to base value Might be useful for "updates"
   * @return
   */
  def apply(
      version: Option[String] = None,
      theme: Option[String] = None,
      language: Option[String] = None,
      startPage: Option[String] = None,
      appliedAt: Option[String] = None,
      hash: Option[String] = None,
      base: Settings,
  ): Settings = {
    new Settings(
      Version(version = version.getOrElse(base.version())),
      App(
        theme = theme.getOrElse(base.app().theme()),
        language = language.getOrElse(base.app().language()),
        startPage = startPage.getOrElse(base.app().startPage()),
      ),
      System(
        Migrations(
          appliedAt = appliedAt.getOrElse(base.system().migrations().appliedAt()),
          hash = hash.getOrElse(base.system().migrations().hash()),
        ),
      ),
    )
  }

  private def requiredTag(node: NodeSeq): String = {
    node.head.text.trim
  }

  private def optionalTag(node: NodeSeq, defaultValue: String = ""): String = {
    if (node.isEmpty) {
      return defaultValue
    }
    node.head.text.trim
  }
}

sealed case class Settings(override val values: Serializable*) extends Serializable {
  protected val tag: String = "settings"

  private val mappedVersion: Option[Version] = mapValue("version", Version.apply)
  private val mappedApp: Option[App] = mapGroup("app", App.apply)
  private val mappedSystem: Option[System] = mapGroup("system", System.apply)

  def XML(): Elem = {
    <settings>
      {for (v <- values) yield v.XML()}
    </settings>
  }

  def version(): String = {
    mappedVersion.get.value
  }

  def app(): App = {
    mappedApp.get
  }

  def system(): System = {
    mappedSystem.get
  }
}
