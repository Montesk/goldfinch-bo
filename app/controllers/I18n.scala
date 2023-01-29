package controllers

import play.api.i18n._

trait I18n extends LangImplicits {
  implicit val lang: Lang = Lang("ru") // TODO: configurable value
  val messages: Messages = lang2Messages
}
