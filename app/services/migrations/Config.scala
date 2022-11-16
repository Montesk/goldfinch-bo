package services.migrations

trait Config {
  val migrationDirPath: String = Config.migrationsDirPath
  val roleCommentPattern: String = Config.roleCommentPatter
  val outputPath: String = Config.outputPath
  val undefinedRoleMessage: String = Config.undefinedRoleMessage
}

object Config {
  val migrationsDirPath: String = "conf/evolutions/default"
  val roleCommentPatter: String = "-- >Role"
  val outputPath: String = "conf/evolutions/migrations.md"
  val undefinedRoleMessage: String = "undefined role"
}

case class DefaultConfig() extends Config {}
