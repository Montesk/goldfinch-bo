package controllers

import javax.inject._

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Lang, Messages}
import play.api.mvc._
import play.api.routing.Router
import com.google.inject.Provider

import services.settings.Settings
import view.models.{SettingsPage => SettingsData}

@deprecated("use v2 SettingsUpdateDataV2 with optional direct settings update")
case class SettingsUpdateData(
    key: String,
    value: String,
)

case class SettingsUpdateDataV2(
    language: String,
    theme: String,
    startPage: String,
    moduleProductsPerPage: String,
    moduleProductsView: String,
)

@Singleton
class SettingsController @Inject() (
    val controllerComponents: ControllerComponents,
    val router: Provider[Router],
) extends BaseController
    with I18n {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(settings = SettingsData(pages = pages())))
  }

  @deprecated
  def edit(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    settingsUpdateForm
      .bindFromRequest()
      .fold(
        formWithErrors => {
          throw new Exception(
            s"Invalid data -> errors: ${formWithErrors.toString}",
          )
        },
        updateData => {
          if (updateData.key != "theme") {
            throw new Exception(s"unsupported update operation for ${updateData.key}")
          }
          Settings.updateAppTheme(updateData.value)
        },
      )

    Ok
  }

  def editV2(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    settingsUpdateFormV2
      .bindFromRequest()
      .fold(
        formWithErrors => {
          throw new Exception(
            s"Invalid data -> errors: ${formWithErrors.toString}",
          )
        },
        updateData => {
          if (updateData.language != Settings.appLanguage()) {
            Settings.updateAppLanguage(updateData.language)
          }

          if (updateData.theme != Settings.appTheme()) {
            Settings.updateAppTheme(updateData.theme)
          }

          if (updateData.startPage != Settings.appStartPage()) {
            Settings.updateAppStartPage(updateData.startPage)
          }

          if (updateData.moduleProductsPerPage.toInt != Settings.moduleProductsPerPage()) {
            Settings.updateModuleProductsPerPage(updateData.moduleProductsPerPage.toInt)
          }

          if (updateData.moduleProductsView != Settings.moduleProductsView()) {
            Settings.updateModuleProductsView(updateData.moduleProductsView)
          }
        },
      )

    val messages: Messages = lang2Messages(Lang(Settings.appLanguage()))
    Redirect(routes.SettingsController.index()).flashing("success" -> Messages("flash.controller.settings")(messages))
  }

  private def pages(): Vector[String] = {
    // A list of method, path pattern and controller/method invocations for each route
    val documentation = router.get().documentation
    val result: Seq[String] = documentation.collect {
      case (method, path, _)
          if (method == "GET" && !path.contains("$") && !path.contains("edit") && !path.contains("assets")) =>
        path
    }
    result.toVector
  }

  @deprecated
  private val settingsUpdateForm: Form[SettingsUpdateData] = Form(
    mapping(
      "key" -> text,
      "value" -> text,
    )(SettingsUpdateData.apply)(SettingsUpdateData.unapply),
  )

  private val settingsUpdateFormV2: Form[SettingsUpdateDataV2] = Form(
    mapping(
      "language" -> text,
      "theme" -> text,
      "startPage" -> text,
      "moduleProductsPerPage" -> text,
      "moduleProductsView" -> text,
    )(SettingsUpdateDataV2.apply)(SettingsUpdateDataV2.unapply),
  )
}
