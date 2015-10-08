name := """stream"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test
)

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.typesafe.play" %% "play-slick" % "1.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.1",
  "org.webjars" % "webjars-locator-core" % "0.28",
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.webjars" % "react" % "0.13.3",
  "org.webjars" % "marked" % "0.3.2-1",
  "org.webjars" % "font-awesome" % "4.4.0",
  "org.webjars" % "lodash" % "3.10.1",
  play.sbt.Play.autoImport.cache
)


resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

routesGenerator := InjectedRoutesGenerator
unmanagedResourceDirectories in Compile += baseDirectory.value / "app" / "views"
