package pkg.dto.settings

import pkg.xml

import scala.xml.Elem

object Modules {
  def apply(nodes: xml.Serializable*): Modules = {
    new Modules(nodes: _*)
  }
}

case class Modules(override val values: xml.Serializable*) extends xml.Serializable {
  protected val tag: String = "modules"

  private val mappedProducts: Option[Products] = mapGroup("products", Products.apply)

  override def XML(): Elem = {
    <modules>
      {for (v <- values) yield v.XML()}
    </modules>
  }

  def products(): Products = {
    mappedProducts.get
  }
}
