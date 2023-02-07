package pkg.dto.settings

import scala.xml.{Elem, NodeSeq}
import pkg.xml.Serializable

object Settings {
  def apply(xml: Elem): Settings = {
    new Settings(
      Version(requiredTag(xml \ "version")),
      App(
        Theme(optionalTag(xml \ "app" \ "theme", "")),
        Language(optionalTag(xml \ "app" \ "language", "")),
        StartPage(optionalTag(xml \ "app" \ "startPage", "")),
      ),
      Modules(
        Products(
          optionalTag(xml \ "modules" \ "products" \ "view", ""),
          optionalTag(xml \ "modules" \ "products" \ "perPage", "0"),
        ),
      ),
      System(
        Migrations(
          AppliedAt(requiredTag(xml \ "system" \ "migrations" \ "appliedAt")),
          Hash(requiredTag(xml \ "system" \ "migrations" \ "hash")),
        ),
      ),
    )
  }

  def apply(values: Serializable*): Settings = {
    new Settings(values: _*)
  }

  /**
   * Values constructor helper preserves standard settings xml form (field orders)
   * @return
   */
  def apply(
      appVersion: String,
      appTheme: String,
      appLanguage: String,
      appStartPage: String,
      systemMigrationsAppliedAt: String,
      systemMigrationsHash: String,
      moduleProductsPerPage: Int,
      moduleProductsView: String,
  ): Settings = {
    new Settings(
      Version(appVersion),
      App(
        theme = appTheme,
        language = appLanguage,
        startPage = appStartPage,
      ),
      Modules(
        Products(
          perPage = moduleProductsPerPage.toString,
          view = moduleProductsView,
        ),
      ),
      System(
        Migrations(
          appliedAt = systemMigrationsAppliedAt,
          hash = systemMigrationsHash,
        ),
      ),
    )
  }

  /**
   * Helper constructor with update values and fallback to base value Might be useful for "updates"
   * @return
   */
  def apply(
      appVersion: Option[String] = None,
      appTheme: Option[String] = None,
      appLanguage: Option[String] = None,
      appStartPage: Option[String] = None,
      systemMigrationsAppliedAt: Option[String] = None,
      systemMigrationsHash: Option[String] = None,
      moduleProductsPerPage: Option[Int] = None,
      moduleProductsView: Option[String] = None,
      base: Settings,
  ): Settings = {
    new Settings(
      Version(version = appVersion.getOrElse(base.version())),
      App(
        theme = appTheme.getOrElse(base.app().theme()),
        language = appLanguage.getOrElse(base.app().language()),
        startPage = appStartPage.getOrElse(base.app().startPage()),
      ),
      Modules(
        Products(
          view = moduleProductsView.getOrElse(base.modules().products().view()),
          perPage = moduleProductsPerPage.getOrElse(base.modules().products().perPage()).toString,
        ),
      ),
      System(
        Migrations(
          appliedAt = systemMigrationsAppliedAt.getOrElse(base.system().migrations().appliedAt()),
          hash = systemMigrationsHash.getOrElse(base.system().migrations().hash()),
        ),
      ),
    )
  }

  private def requiredTag(node: NodeSeq): String = {
    node.head.text.trim
  }

  private def optionalTag(node: NodeSeq, defaultValue: String = ""): String = {
    if (node.isEmpty) {
      return defaultValue
    }
    node.head.text.trim
  }
}

sealed case class Settings(override val values: Serializable*) extends Serializable {
  protected val tag: String = "settings"

  private val mappedVersion: Option[Version] = mapValue("version", Version.apply)
  private val mappedApp: Option[App] = mapGroup("app", App.apply)
  private val mappedSystem: Option[System] = mapGroup("system", System.apply)
  private val mappedModules: Option[Modules] = mapGroup("modules", Modules.apply)

  def XML(): Elem = {
    <settings>
      {for (v <- values) yield v.XML()}
    </settings>
  }

  def version(): String = {
    mappedVersion.get.value
  }

  def app(): App = {
    mappedApp.get
  }

  def system(): System = {
    mappedSystem.get
  }

  def modules(): Modules = {
    mappedModules.get
  }
}
