package pkg.dto.settings

import scala.xml.Elem
import pkg.xml

object App {
  def apply(nodes: xml.Serializable*): App = {
    new App(nodes: _*)
  }

  def apply(theme: String, language: String, startPage: String): App = {
    new App(
      Theme(theme),
      Language(language),
      StartPage(startPage),
    )
  }
}

case class App(override val values: xml.Serializable*) extends xml.Serializable {
  protected val tag: String = "app"

  val theme: Option[Theme] = mapValue("theme", Theme.apply)
  val language: Option[Language] = mapValue("language", Language.apply)
  val startPage: Option[StartPage] = mapValue("startPage", StartPage.apply)

  override def XML(): Elem = {
    <app>
      {for (v <- values) yield v.XML()}
    </app>
  }
}
