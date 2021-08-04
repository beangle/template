import Dependencies._
import BuildSettings._

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/beangle/template"),
    "scm:git@github.com:beangle/template.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "chaostone",
    name  = "Tihua Duan",
    email = "duantihua@gmail.com",
    url   = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "The Beangle Commons Library"
ThisBuild / homepage := Some(url("http://beangle.github.io/template/index.html"))

lazy val root = (project in file("."))
  .settings()
  .aggregate(freemarker)

lazy val freemarker = (project in file("freemarker"))
  .settings(
    name := "beangle-template-freemarker",
    commonSettings,
    libraryDependencies ++= (commonDeps)
  )

publish / skip := true
