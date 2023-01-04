package services.migrations

import java.io.File
import java.nio.file.{Files, Paths}
import org.scalatestplus.play.PlaySpec

class MigrationsSpec extends PlaySpec with MigrationSpecSuite {
  val stubFilesRoles: Map[String, String] = Map(
    "1.sql" -> "create_table_users", // only role comment present
    "2.sql" -> "create_table_products", // contains other comments
    "3.sql" -> mockConfig().undefinedRoleMessage, // undefined role (random sql comment)
    "4.sql" -> "create_table_products_v2" // comment in the end of file
  )

  override def mockConfig(): Config = {
    MockConfig()
  }

  "Migrations" must {
    "Generate real migrations info without exception" in {
      val cfg = mockConfig()

      val m = new Migrations(cfg)
      noException mustBe thrownBy(m.writeInfo())

      cleanup(cfg.outputPath)
    }

    "Generate must contains certain contents" in {
      val cfg = mockConfig()

      val m = new Migrations(cfg)
      noException mustBe thrownBy(m.writeInfo())

      val got = readResult(cfg.outputPath)

      for (case (fileName, role) <- stubFilesRoles) {
        assert(got.contains(fileName) && got.contains(role))
      }

      cleanup(cfg.outputPath)
    }
  }
}

protected sealed trait MigrationSpecSuite {
  val stubFilesRoles: Map[String, String]

  def mockConfig(): Config = {
    DefaultConfig()
  }

  def cleanup(outputPath: String): Unit = {
    Files.delete(Paths.get(outputPath))
  }

  def readResult(outputPath: String): String = {
    val source = scala.io.Source.fromFile(new File(outputPath))
    val fileContents =
      try source.mkString
      finally source.close()
    fileContents
  }
}

protected sealed case class MockConfig() extends Config {
  override val migrationDirPath: String = "test/services/migrations/stubs/evolutions"
  override val outputPath: String = "test/services/migrations/migrations.md"
}
