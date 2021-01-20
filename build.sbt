ThisBuild / resolvers ++= Seq(
  "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal
)

name := "flink-key-by-problem-with-rocksdb-state"

version := "0.1-SNAPSHOT"

organization := "org.example"

ThisBuild / scalaVersion := "2.11.12"

val flinkVersion = "1.11.3"

val dependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % Provided,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % Provided,
  "org.apache.flink" %% "flink-statebackend-rocksdb" % flinkVersion % Provided,
  "org.apache.flink" %% "flink-clients" % flinkVersion % Provided,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test
)

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= dependencies,
  )

//assembly / mainClass := Some("org.example.Job")

// make run command include the provided dependencies
Compile / run  := Defaults.runTask(Compile / fullClasspath,
  Compile / run / mainClass,
  Compile / run / runner
).evaluated

// stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
Compile / run / fork := true
Global / cancelable := true

// exclude Scala library from assembly
assembly / assemblyOption  := (assembly / assemblyOption).value.copy(includeScala = false)

// Needed by WatermarkStrategy.forBoundedOutOfOrderness
scalacOptions += "-target:jvm-1.8"
