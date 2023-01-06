package pkg.dto.settings

import scala.xml.Elem
import pkg.xml

object Version {
  def apply(version: String): Version = new Version(value = version)
}

case class Version(value: String) extends xml.XMLValue[String] with xml.Serializable {
  protected val tag: String = "version"

  def XML(): Elem = {
    <version>
      {value}
    </version>
  }
}
