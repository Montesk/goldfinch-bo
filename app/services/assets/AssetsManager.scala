package services.assets

import java.io.File
import javax.inject.Inject
import com.google.inject.ImplementedBy
import java.nio.file.{Path, Paths, StandardCopyOption, Files => ioFiles}
import com.google.common.io.{Files => helperFiles}

import play.api.libs.Files

@ImplementedBy(classOf[Assets])
trait Manager {
  def getAssetsPath: String

  def getTempPath: String

  def saveTemporaryFile(file: Files.TemporaryFile, group: String, fileName: String = "", replace: Boolean = true, moveFile: Boolean = true): Path

  def persistTempFile(group: String, srcFile: String, destFile: String, moveFile: Boolean = true): Path

  def getGroupAssets(group: String, exclude: List[String] = List()): List[String]

  /**
   * Get file by name ignoring extension
   * fileName param must be passed without extension
   */
  def getFileByName(group: String, fileName: String): String

  def deleteFile(group: String, fileName: String): Unit
}

object DefaultOptions {
  val projectAssetsDir = "public"
  val tempDirPath = "uploads/temp"
  val assetsDirPath = "uploads/assets"
}

case class Options @Inject()(
                              projectAssetsDir: String = "",
                              tempDirPath: String = "",
                              assetsDirPath: String = ""
                            ) {}

class Assets @Inject()(opts: Options = Options()) extends Manager {
  private val projectDirPath: String = if (opts.projectAssetsDir.isEmpty) DefaultOptions.projectAssetsDir else opts.projectAssetsDir
  private val tempDirPath: String = if (opts.tempDirPath.isEmpty) s"${DefaultOptions.projectAssetsDir}/${DefaultOptions.tempDirPath}" else s"${opts.projectAssetsDir}/${opts.tempDirPath}"
  private val assetsDirPath: String = if (opts.assetsDirPath.isEmpty) s"${DefaultOptions.projectAssetsDir}/${DefaultOptions.assetsDirPath}" else s"${opts.projectAssetsDir}/${opts.assetsDirPath}"

  override def getAssetsPath: String = assetsDirPath.replace(s"${projectDirPath}/", "")

  override def getTempPath: String = tempDirPath.replace(s"${projectDirPath}/", "")

  override def saveTemporaryFile(file: Files.TemporaryFile, group: String, fileName: String, replace: Boolean = true, moveFile: Boolean = true): Path = {
    val tmpFilePath = tempFile(group, fileName)
    mkDirAll(tmpFilePath.toString)

    if (moveFile) {
      return file.moveTo(tmpFilePath.toAbsolutePath, replace = replace)
    }

    file.copyTo(tmpFilePath.toAbsolutePath, replace = replace)
  }

  override def persistTempFile(group: String, srcFile: String, destFile: String, moveFile: Boolean = true): Path = {
    mkDirAll(assetFile(group, destFile).toString)

    val temp = new File(tempFile(group, srcFile).toAbsolutePath.toString)
    val dest = new File(assetFile(group, destFile).toAbsolutePath.toString)

    if (moveFile) {
      return ioFiles.move(temp.toPath, dest.toPath, StandardCopyOption.REPLACE_EXISTING)
    }

    ioFiles.copy(temp.toPath, dest.toPath, StandardCopyOption.REPLACE_EXISTING)
  }

  // @param exclude supports only filenames without extension
  override def getGroupAssets(group: String, exclude: List[String] = List()): List[String] = {
    val result = new File(s"""${assetsDirPath}/${group}/""").list()
    if (result ne null) {
      return result.toList.filter(r => !exclude.contains(helperFiles.getNameWithoutExtension(r)))
    }

    List[String]()
  }

  override def getFileByName(group: String, fileName: String): String = {
    val allFiles = getGroupAssets(group)
    for (file <- allFiles) {
      if (helperFiles.getNameWithoutExtension(file) == helperFiles.getNameWithoutExtension(fileName)) {
        return file
      }
    }
    ""
  }

  override def deleteFile(group: String, fileName: String): Unit = {
    ioFiles.delete(assetFile(group, fileName))
  }

  private def mkDirAll(path: String): Unit = {
    new File(path).mkdirs()
  }

  private def tempFile(group: String, fileName: String): Path = {
    Paths.get(s"""${tempDirPath}/${group}/${fileName}""")
  }

  private def assetFile(group: String, fileName: String): Path = {
    Paths.get(s"""${assetsDirPath}/${group}/${fileName}""")
  }
}
