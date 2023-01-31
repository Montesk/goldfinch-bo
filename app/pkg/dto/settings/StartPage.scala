package pkg.dto.settings

import pkg.xml
import scala.xml.Elem

object StartPage {
  def apply(path: String): StartPage = {
    new StartPage(path)
  }
}

case class StartPage(value: String) extends xml.XMLValue[String] with xml.Serializable {
  protected val tag: String = "startPage"

  def XML(): Elem = {
    <startPage>
      {value}
    </startPage>
  }
}
