package services.migrations

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import play.api.Logging

import pkg.dto.{migrations => dto}

class Migrations(config: Config) extends Logging {
  def lastApplied(): dto.LastApplied = {
    import slick.jdbc.H2Profile.api._

    val dbConfig = DatabaseConfig.forConfig[JdbcProfile](config.evolutionsDBConfigPath)
    import dbConfig._

    val query = sql"""SELECT hash, applied_at FROM play_evolutions ORDER BY id DESC LIMIT 1""".as[(String, String)]

    var result: Vector[(String, String)] = Vector()
    try {
      result = Await.result(db.run(query), Duration(10, SECONDS))
    } catch {
      case e: Throwable =>
        logger.error(s"exception on reading evolutions data ${e}")
        return dto.LastApplied(appliedAt = "", hash = "")
    } finally {
      db.close()
    }

    if (result.isEmpty) {
      return dto.LastApplied(appliedAt = "", hash = "")
    }

    dto.LastApplied(appliedAt = result.head._1, hash = result.head._2)
  }

  def writeInfo(): Unit = {
    val file = new File(config.migrationDirPath)

    val list = file.listFiles
      .filter(_.isFile) // directory check
      .map(_.getPath)
      .toList

    var rows: Vector[MigrationInfo] = Vector()

    for (file <- list) {
      val source = scala.io.Source.fromFile(file)
      val lines =
        try source.mkString
        finally source.close()

      val formatted = lines.split("\r\n")

      var parsedName: Option[String] = None
      for (fmt <- formatted; if fmt.contains(config.roleCommentPattern)) {
        parsedName = Option[String](fmt.split(config.roleCommentPattern)(1))
      }

      val fileName = Paths.get(file).getFileName.toString
      rows = rows :+ MigrationInfo(
        fileName = fileName,
        role = parsedName.getOrElse(config.undefinedRoleMessage).trim
      )
    }

    writeToFile(rows)
  }

  private def writeToFile(info: Vector[MigrationInfo]): Unit = {
    val expectCreated: Path = Paths.get(config.outputPath)
    val file = new File(expectCreated.toAbsolutePath.toString)

    var contents: String = "### Auto-generated file DO NOT EDIT \r\n\r\n"

    for (migrationInfo <- info) {
      contents += s"* [${migrationInfo.fileName}] -> ${migrationInfo.role}\r\n"
    }

    Files.write(file.toPath, contents.getBytes(StandardCharsets.UTF_8))
  }
}

private[migrations] case class MigrationInfo(fileName: String, role: String)
