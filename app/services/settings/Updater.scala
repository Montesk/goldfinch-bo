package services.settings

import play.api.Logging
import pkg.dto.{settings => dto}

sealed case class Update(version: String, versionUpdate: (xml.Elem, Options) => (Boolean, xml.Elem))

trait Updater extends Logging {
  private val roadmap: Map[String, Update] = Map(
    "0.0.2" -> Update("0.0.3", v0_0_3),
    "0.0.3" -> Update("0.0.4", v0_0_4),
  )

  def update(
      currentVersion: String,
      targetVersion: String,
      currentSettings: xml.Elem,
      defaults: Options,
  ): (Boolean, xml.Elem) = {
    if (currentVersion == targetVersion) {
      return (true, currentSettings)
    }

    val logic = roadmap.getOrElse(
      currentVersion,
      Update(
        currentVersion,
        (_, _) => {
          logger.error(s"failed to update. missing update logic for version ${currentVersion}")
          (false, null)
        },
      ),
    )

    val (success, updated) = logic.versionUpdate(currentSettings, defaults)

    if (!success) {
      logger.error(s"failed to update from version ${currentVersion} to target: ${targetVersion}")
      return (false, null)
    }

    update(logic.version, targetVersion, updated, defaults)
  }

  def v0_0_3(prev: xml.Elem, defaults: Options): (Boolean, xml.Elem) = {
    val version = "0.0.3"

    val result =
      try {
        val current = dto.Settings(prev)
        val updated = dto.Settings(
          version = Option(version),
          language = Option(defaults.language),
          base = current,
        )

        return (true, updated.XML())
      } catch {
        case e: Throwable => failedUpdate(version, e)
      }

    result
  }

  def v0_0_4(prev: xml.Elem, defaults: Options): (Boolean, xml.Elem) = {
    val version = "0.0.4"

    val result =
      try {
        val current = dto.Settings(prev)
        val updated = dto.Settings(
          version = Option(version),
          startPage = Option(defaults.startPage),
          base = current,
        )

        return (true, updated.XML())
      } catch {
        case e: Throwable => failedUpdate(version, e)
      }

    result
  }

  private def failedUpdate(target: String, e: Throwable): (Boolean, xml.Elem) = {
    logger.error(s"failed to perform $target update with exception $e")
    (false, null)
  }
}
