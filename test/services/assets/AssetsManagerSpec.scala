package services.assets

import java.io.File
import java.nio.file.{Path, Paths, Files => ioFiles}
import java.util.UUID

import org.scalatestplus.play._
import play.api.libs.Files

class AssetsManagerSpec extends PlaySpec {
  val testTempUploadDirPath: String = "test/services/assets/uploads/temp"
  val testAssetsUploadDirPath: String = "test/services/assets/uploads/assets"
  val testStubFileDir: String = "test/services/assets/stubs/files"

  val testAssetsManagerOptions: Options = Options(
    tempDirPath = Option[String](testTempUploadDirPath),
    assetsDirPath = Option[String](testAssetsUploadDirPath)
  )

  "AssetsManager" must {
    "SaveTemporaryFile success" in {
      val assetsManager = new Assets(testAssetsManagerOptions)

      val fileName: String = "product-stub-1.jpg"
      val stubFile = Paths.get(s"$testStubFileDir/$fileName")
      val group: UUID = UUID.fromString("56e0fc4d-8671-45cf-a2a4-a18db65e2cd0")

      val tmp = new Files.TemporaryFile {
        override def path: Path = stubFile

        override def file: File = new File(stubFile.toAbsolutePath.toString)

        override def temporaryFileCreator: Files.TemporaryFileCreator = ???
      }

      val expectCreated: Path = Paths.get(s"$testTempUploadDirPath/$group/$fileName")

      assetsManager.SaveTemporaryFile(tmp, group.toString, fileName, moveFile = false) mustBe expectCreated.toAbsolutePath

      // delete created by test file
      ioFiles.delete(expectCreated)

      // delete created by test directory
      ioFiles.delete(Paths.get(s"$testTempUploadDirPath/$group"))
    }

    "SaveTemporaryFile throw null pointer exception (passed file is null)" in {
      val assetsManager = new Assets(testAssetsManagerOptions)
      a[NullPointerException] mustBe thrownBy(assetsManager.SaveTemporaryFile(null, "", ""))
    }
  }
}