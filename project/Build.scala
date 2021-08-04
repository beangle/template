import sbt.Keys._
import sbt._

object BuildSettings {
  val buildOrganization = "org.beangle.template"
  val buildVersion = "0.0.33-SNAPSHOT"
  val buildScalaVersion = "3.0.1"

  val commonSettings = Seq(
    organization := buildOrganization,
    organizationName := "The Beangle Software",
    licenses += ("GNU Lesser General Public License version 3", new URL("http://www.gnu.org/licenses/lgpl-3.0.txt")),
    startYear := Some(2005),
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    crossPaths := true,

    publishMavenStyle := true,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishM2Configuration := publishM2Configuration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),

    versionScheme := Some("early-semver"),
    pomIncludeRepository := { _ => false }, // Remove all additional repository other than Maven Central from POM
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
    })
}

object Dependencies {
  val logbackVer = "1.2.4"
  val scalatestVer = "3.2.9"
  val servletapiVer = "5.0.0"
  val commonsVer = "5.2.4"
  val freemarkerVer = "2.3.31"

  val scalatest = "org.scalatest" %% "scalatest" % scalatestVer % "test"
  val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackVer % "test"
  val logbackCore = "ch.qos.logback" % "logback-core" % logbackVer % "test"

  val servletapi = "jakarta.servlet" % "jakarta.servlet-api" % servletapiVer
  val commonsCore = "org.beangle.commons" %% "beangle-commons-core" % commonsVer
  var freemarker = "org.freemarker" % "freemarker" %freemarkerVer
  var commonDeps = Seq(commonsCore, logbackClassic, logbackCore, scalatest, servletapi, commonsCore,freemarker)
}

