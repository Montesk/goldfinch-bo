package services.cache

import scala.collection.mutable
import scala.util.{Failure, Success, Try}
import play.api.Logging
import services.config.Config

case class ApplicationConfig(theme: String)

final case class UnsupportedUpdateOperation() extends Exception {
  new Exception(s"unsupported key to update")
}

trait Cache {
  def config: ApplicationConfig = Cache.config()
}

object Cache extends Logging with CacheKeys {
  private val cache: mutable.WeakHashMap[String, Any] = mutable.WeakHashMap.empty

  def config(): ApplicationConfig = {
    cache
      .getOrElseUpdate(CacheKeys.config, ApplicationConfig(Config.appSettings().theme))
      .asInstanceOf[ApplicationConfig]
  }

  def update(key: String, value: String): Try[Option[Exception]] = {
    key match {
      case `cacheKeyTheme` =>
        cache.update(CacheKeys.config, ApplicationConfig(theme = value))
        Success(None)
      case _ =>
        logger.warn(s"trying to update cache value: ${value} by key: ${key}")
        Failure(UnsupportedUpdateOperation())
    }
  }
}

/**
 * Public cache update keys
 */
trait CacheKeys {
  val cacheKeyTheme: String = "theme"
}

private[cache] object CacheKeys {
  val config: String = "config"
}

