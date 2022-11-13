import com.google.inject.AbstractModule
import services.config.Config

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {
  override def configure(): Unit = {
    println("Loading application configuration...")
    val source = scala.io.Source.fromFile(AppSetting.ConfigPath)
    val fileContents = try source.mkString finally source.close()
    Config.readConfig(fileContents)
    println("Application config loaded")
    println("Application start")
  }
}

object AppSetting {
  val ConfigPath: String = "conf/config.yml"
}