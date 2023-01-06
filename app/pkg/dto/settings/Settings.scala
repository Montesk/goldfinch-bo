package pkg.dto.settings

import scala.xml.Elem
import pkg.xml

object Settings {
  def apply(xml: Elem): Settings = {
    new Settings(
      Version((xml \ "version").head.text.trim),
      App(Theme((xml \ "app" \ "theme").head.text.trim)),
      System(
        Migrations(
          AppliedAt((xml \ "system" \ "migrations" \ "appliedAt").head.text.trim),
          Hash((xml \ "system" \ "migrations" \ "hash").head.text.trim)
        )
      )
    )
  }
}

case class Settings(override val values: xml.Serializable*) extends xml.Serializable {
  protected val tag: String = "settings"

  val version: Option[Version] = mapValue("version", Version.apply)
  val app: Option[App] = mapGroup("app", App.apply)
  val system: Option[System] = mapGroup("system", System.apply)

  def XML(): Elem = {
    <settings>
      {for (v <- values) yield v.XML()}
    </settings>
  }
}
