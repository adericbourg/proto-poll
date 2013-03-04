import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "ProtoPoll"
  val appVersion = "0.1.0-SNAPSHOT"

  val appDependencies = Seq(
    javaCore, 
    javaJdbc, 
    javaEbean,
    "mysql" % "mysql-connector-java" % "5.1.22",
    "commons-codec" % "commons-codec" % "1.7",
    "org.markdownj" % "markdownj" % "0.3.0-1.0.2b4",
    "org.mockito" % "mockito-all" % "1.9.0" % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    routesImport += "util.binders.UuidBinder._"
  )
}
