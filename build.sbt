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
  .disablePlugins(AssemblyPlugin)
  .settings(
    settings
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
    assemblySettings,
    libraryDependencies ++= commonDependencies,
    libraryDependencies ++= akkaDependencies,
    unmanagedBase := baseDirectory.value / "lib",
    mainClass in assembly := Some("de.htwg.se.rummi.Rummi"),
  )
  .dependsOn(player, game)


lazy val player = project
  .settings(name := "player",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies,
    libraryDependencies ++= akkaDependencies,
    libraryDependencies ++= databaseDependencies,
    unmanagedBase := baseDirectory.value / "lib",
    mainClass in assembly := Some("de.htwg.se.rummi.player.controller.Application")
  )
  .dependsOn(model)

lazy val game = project
  .settings(name := "game",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies,
    libraryDependencies ++= akkaDependencies,
    libraryDependencies ++= databaseDependencies,
    unmanagedBase := baseDirectory.value / "lib",
    mainClass in assembly := Some("de.htwg.se.rummi.game_service.Application")
  )
  .dependsOn(model)

lazy val model = project
  .settings(name := "model",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies,
    unmanagedBase := baseDirectory.value / "lib")
  .dependsOn()


// DEPENDENCIES
lazy val dependencies =
  new {
    val logbackV = "1.2.3"
    val logstashV = "4.11"
    val scalaLoggingV = "3.7.2"
    val slf4jV = "1.7.26"
    val typesafeConfigV = "1.3.1"
    val akkaV = "2.5.6"
    val scalatestV = "3.0.4"
    val scalacheckV = "1.13.5"
    val akkahttpV = "10.0.7"

    val logback = "ch.qos.logback" % "logback-classic" % logbackV
    val logstash = "net.logstash.logback" % "logstash-logback-encoder" % logstashV
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV
    val slf4j = "org.slf4j" % "jcl-over-slf4j" % slf4jV
    val typesafeConfig = "com.typesafe" % "config" % typesafeConfigV
    val akka = "com.typesafe.akka" %% "akka-stream" % akkaV
    val scalatest = "org.scalatest" %% "scalatest" % scalatestV
    val scalaswing = "org.scala-lang.modules" % "scala-swing_2.12" % "2.0.3"
    val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckV
    val json = "com.typesafe.play" %% "play-json" % "2.6.6"
    val gguice = "com.google.inject" % "guice" % "4.1.0"
    val scalaguice = "net.codingwell" %% "scala-guice" % "4.1.0"
    val akkahttp = "com.typesafe.akka" %% "akka-http" % akkahttpV
    val akkaplayjson = "de.heikoseeberger" %% "akka-http-play-json" % "1.17.0"

    val slick = "com.typesafe.slick" %% "slick" % "3.3.1"
    val slickHikaricp = "com.typesafe.slick" %% "slick-hikaricp" % "3.3.1"
    val h2Database = "com.h2database" % "h2" % "1.4.199"
    val mongoDatabase = "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0"
    val postgresqlDatabase = "org.postgresql" % "postgresql" % "42.2.13"
  }

lazy val akkaDependencies = Seq(
  dependencies.akkaplayjson,
  dependencies.akkahttp,
  dependencies.akka,
)

lazy val databaseDependencies = Seq(
  dependencies.slick,
  dependencies.slickHikaricp,
  dependencies.h2Database,
  dependencies.mongoDatabase,
  dependencies.postgresqlDatabase
)

lazy val commonDependencies = Seq(
  dependencies.gguice,
  dependencies.scalaguice,
  dependencies.scalaswing,
  dependencies.json,
  dependencies.logback,
  dependencies.logstash,
  dependencies.scalaLogging,
  dependencies.slf4j,
  dependencies.typesafeConfig,
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

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  test in assembly := {},
  assemblyMergeStrategy in assembly := {
    case "module-info.class" => MergeStrategy.discard
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case "application.conf" => MergeStrategy.concat
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)