package services.assets

import java.io.File
import java.nio.file.{Path, Paths, Files => ioFiles}
import java.util.UUID

import org.scalatestplus.play._
import play.api.libs.Files
import play.api.libs.Files.TemporaryFile

class AssetsManagerSpec extends PlaySpec {
  val testTempUploadDirPath: String = "test/services/assets/uploads/temp"
  val testAssetsUploadDirPath: String = "test/services/assets/uploads/assets"
  val testStubFileDir: String = "test/services/assets/stubs/files"

  val testAssetsManagerOptions: Options = Options(
    tempDirPath = testTempUploadDirPath,
    assetsDirPath = testAssetsUploadDirPath
  )

  "AssetsManager" must {
    "SaveTemporaryFile success" in {
      val assetsManager = new Assets(testAssetsManagerOptions)

      val fileName: String = "product-stub-1.jpg"
      val group: UUID = UUID.fromString("56e0fc4d-8671-45cf-a2a4-a18db65e2cd0")

      val expectCreated: Path = Paths.get(s"$testTempUploadDirPath/$group/$fileName")

      assetsManager.SaveTemporaryFile(tempFileFromStub(s"$testStubFileDir/$fileName"), group.toString, fileName, moveFile = false) mustBe expectCreated.toAbsolutePath

      noException mustBe thrownBy(new File(expectCreated.toAbsolutePath.toString))

      // delete created by test file
      ioFiles.delete(expectCreated)

      // delete created by test directory
      ioFiles.delete(Paths.get(s"$testTempUploadDirPath/$group"))
    }

    "SaveTemporaryFile throw null pointer exception (passed file is null)" in {
      val assetsManager = new Assets(testAssetsManagerOptions)
      a[NullPointerException] mustBe thrownBy(assetsManager.SaveTemporaryFile(null, "", ""))
    }

    "PersistTempFile success" in {
      val assetsManager = new Assets(testAssetsManagerOptions)

      val srcFileName: String = "product-stub-1.jpg"
      val destFileName: String = "1.jpg"
      val group: UUID = UUID.fromString("56e0fc4d-8671-45cf-a2a4-a18db65e2cd1")

      // create temp file to persist
      noException mustBe thrownBy(assetsManager.SaveTemporaryFile(tempFileFromStub(s"$testStubFileDir/$srcFileName"), group.toString, srcFileName, moveFile = false))

      val expectPath = Paths.get(s"$testAssetsUploadDirPath/$group/$destFileName")

      assetsManager.PersistTempFile(group.toString, srcFileName, destFileName, moveFile = false)

      noException mustBe thrownBy(new File(expectPath.toAbsolutePath.toString))

      // delete temp file
      ioFiles.delete(Paths.get(s"$testTempUploadDirPath/$group/$srcFileName"))

      // delete temp file directory
      ioFiles.delete(Paths.get(s"$testTempUploadDirPath/$group"))

      // delete created by test file
      ioFiles.delete(expectPath)

      // delete created by test directory
      ioFiles.delete(Paths.get(s"$testAssetsUploadDirPath/$group"))
    }
  }

  private def tempFileFromStub(stubName: String): TemporaryFile = {
    val stubFile = Paths.get(stubName)

    new Files.TemporaryFile {
      override def path: Path = stubFile

      override def file: File = new File(stubFile.toAbsolutePath.toString)

      override def temporaryFileCreator: Files.TemporaryFileCreator = ???
    }
  }
}