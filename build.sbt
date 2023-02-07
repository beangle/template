import org.beangle.parent.Dependencies._
import org.beangle.parent.Settings._

ThisBuild / organization := "org.beangle.template"
ThisBuild / version := "0.1.4"

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

ThisBuild / description := "The Beangle Template Library"
ThisBuild / homepage := Some(url("http://beangle.github.io/template/index.html"))

val beangle_commons_ver="5.5.0"
val beangle_commons_core = "org.beangle.commons" %% "beangle-commons-core" % beangle_commons_ver
val beangle_commons_text = "org.beangle.commons" %% "beangle-commons-text" % beangle_commons_ver
val commonDeps = Seq(logback_classic, logback_core, scalatest, beangle_commons_core,beangle_commons_text)

lazy val root = (project in file("."))
  .settings()
  .aggregate(api,pfreemarker)


lazy val api = (project in file("api"))
  .settings(
    name := "beangle-template-api",
    common,
    libraryDependencies ++= (commonDeps)
  )

lazy val pfreemarker = (project in file("freemarker"))
  .settings(
    name := "beangle-template-freemarker",
    common,
    libraryDependencies ++= (commonDeps ++ Seq(freemarker))
  ).dependsOn(api)

publish / skip := true
ThisBuild / Test / parallelExecution := false
