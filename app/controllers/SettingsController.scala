package controllers

import javax.inject._

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import services.settings.Settings

case class SettingsUpdateData(
    key: String,
    value: String
)

@Singleton
class SettingsController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {
  def edit(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    settingsUpdateForm
      .bindFromRequest()
      .fold(
        formWithErrors => {
          throw new Exception(
            s"Invalid data -> errors: ${formWithErrors.toString}"
          )
        },
        updateData => {
          if (updateData.key != "theme") {
            throw new Exception(s"unsupported update operation for ${updateData.key}")
          }
          Settings.updateTheme(updateData.value)
        }
      )

    Ok
  }

  private val settingsUpdateForm: Form[SettingsUpdateData] = Form(
    mapping(
      "key" -> text,
      "value" -> text
    )(SettingsUpdateData.apply)(SettingsUpdateData.unapply)
  )
}
