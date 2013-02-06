import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "ProtoPoll"
  val appVersion = "0.1.0-SNAPSHOT"

  val appDependencies = Seq(
    javaCore, 
    javaJdbc, 
    javaJpa,
    javaEbean,
    "commons-codec" % "commons-codec" % "1.7",
    "org.mockito" % "mockito-all" % "1.9.0" % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    routesImport += "util.binders.UuidBinder._"
  )
}
