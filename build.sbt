ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "3.2.2"

val scalaTestVersion = "3.2.11"
val typeSafeConfigVersion = "1.4.2"
val logbackVersion = "1.2.10"
val sfl4sVersion = "2.0.0-alpha5"
val graphVizVersion = "0.18.1"
val netBuddyVersion = "1.14.4"
val catsVersion = "2.9.0"
val apacheCommonsVersion = "2.13.0"
val jGraphTlibVersion = "1.5.2"
val scalaParCollVersion = "1.0.4"

lazy val commonDependencies = Seq(
  "org.scala-lang.modules" %% "scala-parallel-collections" % scalaParCollVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.scalatestplus" %% "mockito-4-2" % "3.2.12.0-RC2" % Test,
  "com.typesafe" % "config" % typeSafeConfigVersion,
  // Exclude other SLF4J bindings and include only logback-classic
  "ch.qos.logback" % "logback-classic" % logbackVersion excludeAll(
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.slf4j.impl")
  ),
  "net.bytebuddy" % "byte-buddy" % netBuddyVersion,
  "org.apache.hadoop" % "hadoop-common" % "3.3.6",
  "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "3.3.6",
  "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % "3.3.6",
  "org.graphstream" % "gs-core" % "2.0",
  "org.yaml" % "snakeyaml" % "2.0",
  "org.mockito" % "mockito-core" % "5.2.0" % Test,
  "org.apache.mrunit" % "mrunit" % "1.1.0" % Test classifier "hadoop2",
//  "org.apache.hadoop" % "hadoop-aws" % "3.2.1",
//  "software.amazon.awssdk" % "aws-sdk-java" % "2.20.69"
)

val cleanProject = taskKey[Unit]("Cleaning graphs and shards")
cleanProject := {
  val log = streams.value.log
  val directoriesToDelete = Seq(
    baseDirectory.value / "graphs",
    baseDirectory.value / "shards"
  )
  directoriesToDelete.foreach { dir =>
    if (dir.exists()) {
      log.info(s"Deleting contents of ${dir.absolutePath}")
      (dir ** "*").get.foreach( file =>
      if (file != dir) {
        IO.delete(file)
      }
      )
    }
  }
}

lazy val root = (project in file("."))
  .settings(
    name := "graphComparison"
  )

Compile / run / mainClass := Some("app.Main")

unmanagedBase := baseDirectory.value / "src" / "main" / "resources" / "lib"

libraryDependencies ++= commonDependencies

clean := {
  clean.dependsOn(cleanProject).value
}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}

assemblyJarName in assembly := "graphComparison.jar"
