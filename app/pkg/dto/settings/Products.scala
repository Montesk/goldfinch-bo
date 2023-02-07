package pkg.dto.settings

import pkg.xml

import scala.xml.Elem

object Products {
  def apply(nodes: xml.Serializable*): Products = {
    new Products(nodes: _*)
  }

  def apply(view: String, perPage: String): Products = {
    new Products(
      View(view),
      PerPage(perPage),
    )
  }
}

case class Products(override val values: xml.Serializable*) extends xml.Serializable {
  protected val tag: String = "products"

  private val mappedView: Option[View] = mapValue("view", View.apply)
  private val mappedPerPage: Option[PerPage] = mapValue("perPage", PerPage.apply)

  override def XML(): Elem = {
    <products>
      {for (v <- values) yield v.XML()}
    </products>
  }

  def view(): String = {
    mappedView.get.value
  }

  def perPage(): String = {
    mappedPerPage.get.value
  }
}
