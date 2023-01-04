package services.cache

import scala.collection.mutable

class Cache {
  private val cache: mutable.HashMap[String, Any] =
    mutable.HashMap.empty

  def get[T](key: String): T = {
    cache(key).asInstanceOf[T]
  }

  def get[T](key: String, orElse: T): T = {
    cache
      .getOrElseUpdate(
        key,
        orElse
      )
      .asInstanceOf[T]
  }

  def getOrCall[T](key: String, orElseFn: () => T): T = {
    cache
      .getOrElseUpdate(
        key,
        orElseFn()
      )
      .asInstanceOf[T]
  }

  def update[T](key: String, value: T): Unit = {
    cache.update(key, value)
  }
}
