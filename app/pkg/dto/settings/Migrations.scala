package pkg.dto.settings

import scala.xml.Elem
import pkg.xml

object Migrations {
  def apply(nodes: xml.Serializable*): Migrations = {
    new Migrations(nodes: _*)
  }

  def apply(appliedAt: String, hash: String): Migrations = {
    new Migrations(
      AppliedAt(appliedAt),
      Hash(hash),
    )
  }
}

case class Migrations(override val values: xml.Serializable*) extends xml.Serializable {
  protected val tag: String = "migrations"

  private val mappedAppliedAt: Option[AppliedAt] = mapValue("appliedAt", AppliedAt.apply)
  private val mappedHash: Option[Hash] = mapValue("hash", Hash.apply)

  override def XML(): Elem = {
    <migrations>
      {for (v <- values) yield v.XML()}
    </migrations>
  }

  def appliedAt(): String = {
    mappedAppliedAt.get.value
  }

  def hash(): String = {
    mappedHash.get.value
  }
}
