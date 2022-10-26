package services.assets

import java.io.File
import java.nio.file.{Path, Paths, StandardCopyOption, Files => ioFiles}

import play.api.libs.Files

trait Manager {
  def SaveTemporaryFile(file: Files.TemporaryFile, group: String, fileName: String = "", replace: Boolean = true): Path

  def PersistTempFile(group: String, srcFile: String, destFile: String): Unit

  def GetGroupAssets(group: String, exclude: List[String] = List()): List[String]

  /**
   * Get file by name ignoring extension
   * fileName param must be passed without extension
   */
  def GetFileByName(group: String, fileName: String): String
}

class Assets() extends Manager {
  private val tempDirPath: String = "public/uploads/temp"
  private val assetsDirPath: String = "public/uploads/assets"

  override def SaveTemporaryFile(file: Files.TemporaryFile, group: String, fileName: String, replace: Boolean = true): Path = {
    val tmpFilePath = tempFile(group, fileName)
    mkDirAll(tmpFilePath.toString)

    file.moveTo(tmpFilePath.toAbsolutePath, replace = replace)
  }

  override def PersistTempFile(group: String, srcFile: String, destFile: String): Unit = {
    mkDirAll(assetFile(group, destFile).toString)

    val temp = new File(tempFile(group, srcFile).toAbsolutePath.toString)
    val dest = new File(assetFile(group, destFile).toAbsolutePath.toString)

    ioFiles.move(temp.toPath, dest.toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
  }

  override def GetGroupAssets(group: String, exclude: List[String] = List()): List[String] = {
    val result = new File(s"""${assetsDirPath}/${group}/""").list()
    if (result ne null) {
      return result.toList.filter(r => !exclude.contains(r))
    }

    List[String]()
  }

  override def GetFileByName(group: String, fileName: String): String = {
    val allFiles = GetGroupAssets(group)
    for (fileName <- allFiles) {
      val withoutExtension = fileName.split("\\.")(0)
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
