package services.cache

import org.scalatestplus.play.PlaySpec
import services.config.Config

class CacheSpec extends PlaySpec with CacheKeys {
  val mockConfig: String =
    """
      |app:
      |  theme: "green"
      |repository:
      |  defaults:
      |    dbWaitTimeout: "12s"
      |""".stripMargin

  "Cache segments read and update" must {
    "default app configuration must be loaded" in {
      Config.readConfig(mockConfig)
      Cache.config() equals ApplicationConfig(theme = Config.appSettings().theme)
    }

    "default app configuration must be loaded, then updated, get cached results" in {
      Config.readConfig(mockConfig)
      Cache.update(cacheKeyTheme, "brown")
      Cache.config() equals ApplicationConfig(theme = "brown")
    }

    "default app configuration must be loaded, then updated, unsupported update" in {
      Config.readConfig(mockConfig)
      val got = Cache.update("unsupported_cache_key", "violet")
      assert(got.isFailure)
      an[UnsupportedUpdateOperation] must be thrownBy got.get
    }
  }
}