package pkg.dto.settings

import scala.xml.Elem
import pkg.xml

object Theme {
  def apply(theme: String): Theme = {
    new Theme(theme)
  }
}

case class Theme(value: String) extends xml.XMLValue[String] with xml.Serializable {
  protected val tag: String = "theme"

  def XML(): Elem = {
    <theme>
      {value}
    </theme>
  }
}
