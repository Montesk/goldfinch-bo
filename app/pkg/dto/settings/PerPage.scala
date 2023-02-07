package pkg.dto.settings

import pkg.xml

import scala.xml.Elem

object PerPage {
  def apply(perPage: String): PerPage = {
    new PerPage(perPage)
  }
}

case class PerPage(value: String) extends xml.XMLValue[String] with xml.Serializable {
  protected val tag: String = "perPage"

  def XML(): Elem = {
    <perPage>
      {value}
    </perPage>
  }
}
