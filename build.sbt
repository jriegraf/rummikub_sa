import sbt.Keys.unmanagedBase

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "de.JuPa"
ThisBuild / organizationName := "JuPa"
ThisBuild / trackInternalDependencies := TrackLevel.TrackIfMissing
ThisBuild / exportJars := true


name := "rummikub"
organization in ThisBuild := "JuPa.Software"

lazy val global = project
  .in(file("."))
  .settings(settings,
    test in assembly := {}
  )
  .aggregate(
    main,
    player,
    game,
    model
  )

lazy val main = project
  .settings(name := "main",
    settings,
    libraryDependencies ++= commonDependencies,
    unmanagedBase := baseDirectory.value / "lib",
    mainClass in assembly := Some("de.htwg.se.rummi.Rummi"),
    assemblyJarName in assembly := "rummi.jar",
  )
  .dependsOn(player, game)


lazy val player = project
  .settings(name := "player",
    settings,
    libraryDependencies ++= commonDependencies,
    unmanagedBase := baseDirectory.value / "lib",
    mainClass in assembly := Some("de.htwg.se.rummi.player_service.controller.Application")
  )
  .dependsOn(model)

lazy val game = project
  .settings(name := "game",
    settings,
    libraryDependencies ++= commonDependencies,
    unmanagedBase := baseDirectory.value / "lib",
    mainClass in assembly := Some("de.htwg.se.rummi.game_service.Application")
  )
      .dependsOn(model)

lazy val model = project
  .settings(name := "model",
    settings,
    libraryDependencies ++= commonDependencies,
    unmanagedBase := baseDirectory.value / "lib")
  .dependsOn()


// DEPENDENCIES
lazy val dependencies =
  new {
    val logbackV = "1.2.3"
    val logstashV = "4.11"
    val scalaLoggingV = "3.7.2"
    val slf4jV = "1.7.25"
    val typesafeConfigV = "1.3.1"
    val pureconfigV = "0.8.0"
    val monocleV = "1.4.0"
    val akkaV = "2.5.6"
    val scalatestV = "3.0.4"
    val scalacheckV = "1.13.5"
    val akkahttpV = "10.0.7"

    val scalaXml = "org.scala-lang.modules" % "scala-xml_2.12" % "1.0.6"
    val logback = "ch.qos.logback" % "logback-classic" % logbackV
    val logstash = "net.logstash.logback" % "logstash-logback-encoder" % logstashV
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV
    val slf4j = "org.slf4j" % "jcl-over-slf4j" % slf4jV
    val typesafeConfig = "com.typesafe" % "config" % typesafeConfigV
    val akka = "com.typesafe.akka" %% "akka-stream" % akkaV
    val monocleCore = "com.github.julien-truffaut" %% "monocle-core" % monocleV
    val monocleMacro = "com.github.julien-truffaut" %% "monocle-macro" % monocleV
    val pureconfig = "com.github.pureconfig" %% "pureconfig" % pureconfigV
    val scalatest = "org.scalatest" %% "scalatest" % scalatestV
    val scalaswing = "org.scala-lang.modules" % "scala-swing_2.12" % "2.0.3"
    val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckV
    val json = "com.typesafe.play" %% "play-json" % "2.9.0"
    val gguice = "com.google.inject" % "guice" % "4.2.3"
    val scalaguice = "net.codingwell" %% "scala-guice" % "4.2.3"
    val akkahttp = "com.typesafe.akka" %% "akka-http" % akkahttpV
    val akkaplayjson = "de.heikoseeberger" %% "akka-http-circe" % "1.31.0"
  }

lazy val commonDependencies = Seq(
  dependencies.akkaplayjson,
  dependencies.akkahttp,
  dependencies.akka,
  dependencies.scalaXml,
  dependencies.gguice,
  dependencies.scalaguice,
  dependencies.scalaswing,
  dependencies.json,
  dependencies.logback,
  dependencies.logstash,
  dependencies.scalaLogging,
  dependencies.slf4j,
  dependencies.typesafeConfig,
  dependencies.akka,
  dependencies.scalatest % "test",
  dependencies.scalacheck % "test"
)

// SETTINGS

lazy val settings =
  commonSettings


lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)