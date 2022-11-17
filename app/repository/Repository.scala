package repository

import services.config.{
  Config,
  Repositories => ConfigRepositories,
  Repository => ConfigRepository
}

import scala.concurrent.duration.Duration

trait Repository {
  val config: ConfigRepositories = Config.repositories()

  def withDefaultTimeout(repoConf: Option[ConfigRepository]): Duration = {
    repoConf.getOrElse(config.defaults).dbWaitTimeout
  }
}
