package pkg.xml

import scala.xml.Elem

trait Serializable extends {
  val values: Seq[Serializable] = Seq[Serializable]()
  protected val tag: String

  def XML(): Elem

  protected def mapGroup[T1, T2](tag: String, builder: T2 => T1): Option[T1] = {
    for (v <- values) {
      if (v.tag == tag) {
        return Option[T1](builder(v.values.asInstanceOf[T2]))
      }
    }
    None
  }

  protected def mapValue[T1, T2](tag: String, builder: T2 => T1): Option[T1] = {
    for (v <- values) {
      if (v.tag == tag) {
        return Option[T1](builder(v.asInstanceOf[XMLValue[T2]].value))
      }
    }
    None
  }
}
