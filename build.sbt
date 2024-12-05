import org.beangle.parent.Dependencies.*
import org.beangle.parent.Settings.*

ThisBuild / organization := "org.beangle.template"
ThisBuild / version := "0.1.22-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/beangle/template"),
    "scm:git@github.com:beangle/template.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "The Beangle Template Library"
ThisBuild / homepage := Some(url("http://beangle.github.io/template/index.html"))

val beangle_commons = "org.beangle.commons" % "beangle-commons" % "5.6.23"

lazy val api = (project in file("."))
  .settings(
    name := "beangle-template",
    common,
    libraryDependencies ++= Seq(beangle_commons, freemarker % "optional"),
    libraryDependencies ++= Seq(scalatest, logback_classic % "test")
  )

ThisBuild / Test / parallelExecution := false
