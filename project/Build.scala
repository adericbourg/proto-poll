import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "ProtoPoll"
  val appVersion = "0.1.0-SNAPSHOT"

  val appDependencies = Seq(
    "commons-codec" % "commons-codec" % "1.7",
    "org.mockito" % "mockito-all" % "1.9.0" % "test"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
    routesImport += "util.binders.UuidBinder._"
  )
}
