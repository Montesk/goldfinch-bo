name := """goldfinch"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  guice,
  filters,
  "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4",
  "org.webjars" % "bootstrap" % "4.3.1" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "3.3.1-2",
  "org.webjars" % "font-awesome" % "4.7.0",
  "org.webjars" % "bootstrap-datepicker" % "1.4.0" exclude("org.webjars", "bootstrap"),
  "org.webjars" % "Magnific-Popup" % "1.1.0",
  "org.scalatest" %% "scalatest" % "3.2.14" % Test withSources(),
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test withSources(),
  "com.typesafe.play" %% "play-slick" % "5.1.0" withSources(),
  "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0" withSources(),
  "org.postgresql" % "postgresql" % "42.5.0" withSources(),
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
