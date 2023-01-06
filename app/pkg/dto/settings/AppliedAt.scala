package pkg.dto.settings

import scala.xml.Elem
import pkg.xml

object AppliedAt {
  def apply(timeStr: String): AppliedAt = {
    new AppliedAt(timeStr)
  }
}

case class AppliedAt(value: String) extends xml.XMLValue[String] with xml.Serializable {
  protected val tag: String = "appliedAt"

  def XML(): Elem = {
    <appliedAt>
      {value}
    </appliedAt>
  }
}
