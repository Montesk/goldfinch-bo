package controllers

import play.api.i18n._
import services.settings.Settings

trait I18n extends LangImplicits {
  def lang: Lang = Lang(Settings.language())
  implicit def messages: Messages = lang2Messages(lang)
}
