package services.settings

import play.api.Logging
import pkg.dto.{settings => dto}

sealed case class Update(version: String, versionUpdate: (xml.Elem, Options) => (Boolean, xml.Elem))

trait Updater extends Logging {
  private val roadmap: Map[String, Update] = Map(
    "0.0.2" -> Update("0.0.3", v0_0_3),
    "0.0.3" -> Update("0.0.4", v0_0_4),
    "0.0.4" -> Update("0.0.5", v0_0_5),
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

    val (success, updated) =
      try {
        logic.versionUpdate(currentSettings, defaults)
      } catch {
        case e: Throwable => failedUpdate(logic.version, e)
      }

    if (!success) {
      logger.error(s"failed to update from version ${currentVersion} to target: ${targetVersion}")
      return (false, null)
    }

    update(logic.version, targetVersion, updated, defaults)
  }

  private[settings] def v0_0_3(prev: xml.Elem, defaults: Options): (Boolean, xml.Elem) = {
    val version = "0.0.3"

    val current = dto.Settings(prev)
    val updated = dto.Settings(
      appVersion = Option(version),
      appLanguage = Option(defaults.appLanguage),
      base = current,
    )

    (true, updated.XML())
  }

  private[settings] def v0_0_4(prev: xml.Elem, defaults: Options): (Boolean, xml.Elem) = {
    val version = "0.0.4"

    val current = dto.Settings(prev)
    val updated = dto.Settings(
      appVersion = Option(version),
      appStartPage = Option(defaults.appStartPage),
      base = current,
    )

    (true, updated.XML())
  }

  private[settings] def v0_0_5(prev: xml.Elem, defaults: Options): (Boolean, xml.Elem) = {
    val version = "0.0.5"

    val current = dto.Settings(prev)
    val updated = dto.Settings(
      appVersion = Option(version),
      moduleProductsPerPage = Option(defaults.moduleProductsPerPage),
      moduleProductsView = Option(defaults.moduleProductsView),
      base = current,
    )

    (true, updated.XML())
  }

  private def failedUpdate(target: String, e: Throwable): (Boolean, xml.Elem) = {
    logger.error(s"failed to perform $target update with exception $e")
    (false, null)
  }
}
