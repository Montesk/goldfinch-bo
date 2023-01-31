package pkg.dto.settings

import scala.xml.Elem
import pkg.xml

object Language {
  def apply(language: String): Language = {
    new Language(language)
  }
}

case class Language(value: String) extends xml.XMLValue[String] with xml.Serializable {
  protected val tag: String = "language"

  def XML(): Elem = {
    <language>
      {value}
    </language>
  }
}
