import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd, CmdLike}

import com.goshoplane.sbt.standard.libraries.StandardLibraries._

name := """higgs"""

version := "0.0.1"

scalaVersion := Version.scala

lazy val core = (project in file("core")).disablePlugins(DockerPlugin)
  .settings(
    name := "higgs-core",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % Version.play
    ) ++ Libs.playJson
  )

lazy val search = (project in file("search")).disablePlugins(DockerPlugin)
  .settings(
    name := "higgs-search"
  ).dependsOn(core)

lazy val auth = (project in file("modules/auth")).enablePlugins(PlayScala).dependsOn(core)

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
    ) ++ Libs.akka ++ Libs.microservice,
    dockerExposedPorts := Seq(9000),
    // TODO: remove echo statement once verified
    dockerEntrypoint := Seq("sh", "-c", "export HIGGS_HOST=`/sbin/ifconfig eth0 | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1 }'` && echo $HIGGS_HOST && bin/higgs $*"),
    dockerRepository := Some("docker"),
    dockerBaseImage := "shoplane/baseimage",
    dockerCommands ++= Seq(
      Cmd("USER", "root")
    )
  )
  .dependsOn(core, auth, search)
  .aggregate(core, auth, search)

scalacOptions ++= Seq("-feature",  "-language:postfixOps", "-language:reflectiveCalls")

routesGenerator := InjectedRoutesGenerator