package pkg.dto.settings

import scala.xml.Elem
import pkg.xml

object App {
  def apply(nodes: xml.Serializable*): App = {
    new App(nodes: _*)
  }
}

case class App(override val values: xml.Serializable*) extends xml.Serializable {
  protected val tag: String = "app"

  val theme: Option[Theme] = mapValue("theme", Theme.apply)

  override def XML(): Elem = {
    <app>
      {for (v <- values) yield v.XML()}
    </app>
  }
}
