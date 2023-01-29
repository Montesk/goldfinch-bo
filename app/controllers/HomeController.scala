package controllers

import javax.inject._

import play.api.mvc._

@Singleton
class HomeController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController
    with I18n {
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
}
