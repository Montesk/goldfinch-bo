package pkg.dto.settings

import pkg.xml

import scala.xml.Elem

object View {
  def apply(view: String): View = {
    new View(view)
  }
}

case class View(value: String) extends xml.XMLValue[String] with xml.Serializable {
  protected val tag: String = "view"

  def XML(): Elem = {
    <view>
      {value}
    </view>
  }
}
