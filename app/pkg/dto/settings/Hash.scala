package pkg.dto.settings

import scala.xml.Elem
import pkg.xml

object Hash {
  def apply(hashStr: String): Hash = {
    new Hash(hashStr)
  }
}

case class Hash(value: String) extends xml.XMLValue[String] with xml.Serializable {
  protected val tag: String = "hash"

  def XML(): Elem = {
    <hash>
      {value}
    </hash>
  }
}
