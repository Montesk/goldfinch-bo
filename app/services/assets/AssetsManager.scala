package services.assets

import java.io.File
import javax.inject.Inject
import com.google.inject.ImplementedBy
import java.nio.file.{Path, Paths, StandardCopyOption, Files => ioFiles}
import com.google.common.io.{Files => helperFiles}

import play.api.libs.Files

@ImplementedBy(classOf[Assets])
trait Manager {
  def SaveTemporaryFile(file: Files.TemporaryFile, group: String, fileName: String = "", replace: Boolean = true, moveFile: Boolean = true): Path

  def PersistTempFile(group: String, srcFile: String, destFile: String, moveFile: Boolean = true): Path

  def GetGroupAssets(group: String, exclude: List[String] = List()): List[String]

  /**
   * Get file by name ignoring extension
   * fileName param must be passed without extension
   */
  def GetFileByName(group: String, fileName: String): String
}

object DefaultOptions {
  val tempDirPath = "public/uploads/temp"
  val assetsDirPath = "public/uploads/assets"
}

case class Options @Inject()(
                              tempDirPath: String = "",
                              assetsDirPath: String = ""
                            ) {}

class Assets @Inject()(opts: Options = Options()) extends Manager {
  private val tempDirPath: String = if (opts.tempDirPath.isEmpty) DefaultOptions.tempDirPath else opts.tempDirPath
  private val assetsDirPath: String = if (opts.assetsDirPath.isEmpty) DefaultOptions.assetsDirPath else opts.assetsDirPath

  override def SaveTemporaryFile(file: Files.TemporaryFile, group: String, fileName: String, replace: Boolean = true, moveFile: Boolean = true): Path = {
    val tmpFilePath = tempFile(group, fileName)
    mkDirAll(tmpFilePath.toString)

    if (moveFile) {
      return file.moveTo(tmpFilePath.toAbsolutePath, replace = replace)
    }

    file.copyTo(tmpFilePath.toAbsolutePath, replace = replace)
  }

  override def PersistTempFile(group: String, srcFile: String, destFile: String, moveFile: Boolean = true): Path = {
    mkDirAll(assetFile(group, destFile).toString)

    val temp = new File(tempFile(group, srcFile).toAbsolutePath.toString)
    val dest = new File(assetFile(group, destFile).toAbsolutePath.toString)

    if (moveFile) {
      return ioFiles.move(temp.toPath, dest.toPath, StandardCopyOption.REPLACE_EXISTING)
    }

    ioFiles.copy(temp.toPath, dest.toPath, StandardCopyOption.REPLACE_EXISTING)
  }

  // @param exclude supports only filenames without extension
  override def GetGroupAssets(group: String, exclude: List[String] = List()): List[String] = {
    val result = new File(s"""${assetsDirPath}/${group}/""").list()
    if (result ne null) {
      return result.toList.filter(r => !exclude.contains(helperFiles.getNameWithoutExtension(r)))
    }

    List[String]()
  }

  override def GetFileByName(group: String, fileName: String): String = {
    val allFiles = GetGroupAssets(group)
    for (fileName <- allFiles) {
      val withoutExtension = helperFiles.getNameWithoutExtension(fileName)
      if (withoutExtension == "main") {
        return fileName
      }
    }
    ""
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
