import sbt._
import Keys._
import play.Project._

import de.johoop.jacoco4sbt._
import JacocoPlugin._



object ApplicationBuild extends Build {

  val appName = "ProtoPoll"
  val appVersion = "0.1.0"

  val appDependencies = Seq(
    javaCore, 
    javaJdbc, 
    javaEbean,
    "mysql" % "mysql-connector-java" % "5.1.22",
    "commons-codec" % "commons-codec" % "1.7",
    "org.markdownj" % "markdownj" % "0.3.0-1.0.2b4",
    "org.mockito" % "mockito-all" % "1.9.0" % "test"
  )

  lazy val s = Defaults.defaultSettings ++ Seq(jacoco.settings:_*)

  val main = play.Project(appName, appVersion, appDependencies, settings = s).settings(
    // Code coverage.
    parallelExecution     in jacoco.Config := false,
    jacoco.reportFormats  in jacoco.Config := Seq(XMLReport("utf-8"), HTMLReport("utf-8")),
    jacoco.excludes       in jacoco.Config := Seq("views.*", "controllers.Reverse*", "controllers.javascript.*", "controllers.ref.*", "Routes*"),
    
    // Custom binders for routes 
    routesImport += "util.binders.UuidBinder._"
  )
}
