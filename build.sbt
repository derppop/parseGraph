ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "parseGraph"
  )
mainClass in Compile := Some("Main")

unmanagedBase := baseDirectory.value / "src" / "main" / "resources" / "lib"