package services.config

import scala.concurrent.duration.Duration

import cats.syntax.either._
import io.circe.{Json, ParsingFailure}
import io.circe.generic.auto._
import io.circe.yaml.parser

trait Config {
  val repositories: Repositories = Config.repositories()
  val appSettings: AppSettings = Config.appSettings()

  def readConfig(contentYAML: String): Unit = Config.readConfig(contentYAML)
}

object Config {
  private var loaded: Option[fromConfig] = None
  private var parsed: Option[Repositories] = None

  def readConfig(contentYAML: String): Unit = {
    val json: Either[ParsingFailure, Json] = parser.parse(contentYAML)

    val raw = json.flatMap(_.as[fromConfig]).valueOr(throw _)

    loaded = Option(raw)

    parsed = Option(
      Repositories(
        defaults =
          repositoryWithDuration(Option(loaded.get.repository.defaults)).get,
        delivery = repositoryWithDuration(loaded.get.repository.delivery),
        product = repositoryWithDuration(loaded.get.repository.product),
        order = repositoryWithDuration(loaded.get.repository.order)
      )
    )
  }

  def repositories(): Repositories = {
    parsed.get
  }

  def appSettings(): AppSettings = {
    AppSettings(theme = loaded.get.app.theme)
  }

  private def repositoryWithDuration(
      repo: Option[ConfigRepository]
  ): Option[Repository] = {
    if (repo.isEmpty) {
      return None
    }

    Option(Repository(dbWaitTimeout = Duration(repo.get.dbWaitTimeout)))
  }
}

case class AppSettings(theme: String)

case class Repository(dbWaitTimeout: Duration)

case class Repositories(
    defaults: Repository,
    delivery: Option[Repository] = None,
    product: Option[Repository] = None,
    order: Option[Repository] = None
)

/** Yaml field must match case classes listed below params
  */
private[config] case class fromConfig(
    repository: ConfigRepositories,
    app: ConfigAppSettings
) {}

private[config] case class ConfigRepository(dbWaitTimeout: String)

private[config] case class ConfigRepositories(
    defaults: ConfigRepository,
    delivery: Option[ConfigRepository] = None,
    product: Option[ConfigRepository] = None,
    order: Option[ConfigRepository] = None
)

private[config] case class ConfigAppSettings(theme: String)
