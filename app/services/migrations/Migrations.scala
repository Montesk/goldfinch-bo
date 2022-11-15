package services.migrations

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

class Migrations(cfg: Config) {
  def generateInfo(): Unit = {
    val file = new File(cfg.migrationDirPath)

    val list = file
      .listFiles
      .filter(_.isFile) // directory check
      .map(_.getPath)
      .toList

    var rows: Vector[MigrationInfo] = Vector()

    for (file <- list) {
      val source = scala.io.Source.fromFile(file)
      val lines = try source.mkString finally source.close()

      val formatted = lines.split("\r\n")

      var parsedName: Option[String] = None
      for (fmt <- formatted; if fmt.contains(cfg.roleCommentPattern)) {
        parsedName = Option[String](fmt.split(cfg.roleCommentPattern)(1))
      }

      val fileName = Paths.get(file).getFileName.toString
      rows = rows :+ MigrationInfo(fileName = fileName, role = parsedName.getOrElse(cfg.undefinedRoleMessage).trim)
    }

    writeInfo(rows)
  }

  private def writeInfo(info: Vector[MigrationInfo]): Unit = {
    val expectCreated: Path = Paths.get(cfg.outputPath)
    val file = new File(expectCreated.toAbsolutePath.toString)

    var contents: String = "### Auto-generated file DO NOT EDIT \r\n\r\n"

    for (migrationInfo <- info) {
      contents += s"* [${migrationInfo.fileName}] -> ${migrationInfo.role}\r\n"
    }

    Files.write(file.toPath, contents.getBytes(StandardCharsets.UTF_8))
  }
}

private case class MigrationInfo(fileName: String, role: String)
