import org.beangle.parent.Dependencies.*
import org.beangle.parent.Settings.*

organization := "org.beangle.template"
version := "0.2.9"

scmInfo := Some(
  ScmInfo(
    uri("https://github.com/beangle/template"),
    "scm:git@github.com:beangle/template.git"
  )
)

developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = uri("http://github.com/duantihua")
  )
)

description := "The Beangle Template Library"
homepage := Some(uri("http://beangle.github.io/template/index.html"))

val beangle_commons = "org.beangle.commons" % "beangle-commons" % "6.2.1"

lazy val api = (project in file("."))
  .settings(
    name := "beangle-template",
    common,
    libraryDependencies ++= Seq(beangle_commons, slf4j, freemarker % "optional"),
    libraryDependencies ++= Seq(scalatest, logback_classic % "test"),
    Test / parallelExecution := false
  )
