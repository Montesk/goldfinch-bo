package pkg.dto.settings

import scala.xml.{Elem, NodeSeq}
import pkg.xml

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

case class Settings(override val values: xml.Serializable*) extends xml.Serializable {
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
