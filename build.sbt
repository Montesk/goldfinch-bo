name := """goldfinch"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

pipelineStages := Seq(rjs)

scalaVersion := "2.13.8"

Test / javaOptions += "-Dconfig.file=conf/application.test.conf"

libraryDependencies ++= Seq(
  guice,
  filters,
  "org.webjars" % "bootstrap" % "4.6.2" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "3.6.1",
  "org.webjars" % "font-awesome" % "6.2.0",
  "org.webjars" % "bootstrap-datepicker" % "1.9.0" exclude("org.webjars", "bootstrap"),
  "org.webjars.npm" % "filepond" % "4.30.3",
  "org.webjars" % "Magnific-Popup" % "1.1.0",
  "org.webjars" % "requirejs" % "2.3.6",
  "org.scalatest" %% "scalatest" % "3.2.14" % Test withSources(),
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test withSources(),
  "com.typesafe.play" %% "play-slick" % "5.1.0" withSources(),
  "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0" withSources(),
  "org.postgresql" % "postgresql" % "42.5.0" withSources(),
  "io.circe" %% "circe-yaml" % "0.14.2" withSources(),
  "io.circe" %% "circe-generic" % "0.14.3" withSources()
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
