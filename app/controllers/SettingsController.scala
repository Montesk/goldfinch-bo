package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class SettingsController @Inject()(
                                    val controllerComponents: ControllerComponents,
                                  ) extends BaseController {
  def edit(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    ???
  }
}
