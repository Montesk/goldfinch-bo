package pkg.dto.settings

import scala.xml.Elem
import pkg.xml

object System {
  def apply(nodes: xml.Serializable*): System = {
    new System(nodes: _*)
  }
}

case class System(override val values: xml.Serializable*) extends xml.Serializable {
  protected val tag: String = "system"

  private val mappedMigrations: Option[Migrations] = mapGroup("migrations", Migrations.apply)

  override def XML(): Elem = {
    <system>
      {for (v <- values) yield v.XML()}
    </system>
  }

  def migrations(): Migrations = {
    mappedMigrations.get
  }
}
