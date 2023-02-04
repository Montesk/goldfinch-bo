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

  private val mappedTheme: Option[Theme] = mapValue("theme", Theme.apply)
  private val mappedLanguage: Option[Language] = mapValue("language", Language.apply)
  private val mappedStartPage: Option[StartPage] = mapValue("startPage", StartPage.apply)

  override def XML(): Elem = {
    <app>
      {for (v <- values) yield v.XML()}
    </app>
  }

  def theme(): String = {
    mappedTheme.get.value
  }

  def language(): String = {
    mappedLanguage.get.value
  }

  def startPage(): String = {
    mappedStartPage.get.value
  }
}
