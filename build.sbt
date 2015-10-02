import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd, CmdLike}

name := """higgs"""

version := "0.0.1"

scalaVersion := "2.11.7"

lazy val core = (project in file("core")).disablePlugins(DockerPlugin)
  .settings(
    name := "higgs-core",
    libraryDependencies ++= Seq(
    ) ++ Seq("com.typesafe.play" %% "play-json" % "2.4.3", "com.typesafe.play" %% "play" % "2.4.3")
  )

lazy val auth = (project in file("modules/auth")).enablePlugins(PlayScala).dependsOn(core)

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(PlayScala)
  .settings(
    dockerExposedPorts := Seq(9000),
    // TODO: remove echo statement once verified
    dockerEntrypoint := Seq("sh", "-c", "export HIGGS_HOST=`/sbin/ifconfig eth0 | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1 }'` && echo $HIGGS_HOST && bin/higgs $*"),
    dockerRepository := Some("docker"),
    dockerBaseImage := "shoplane/baseimage",
    dockerCommands ++= Seq(
      Cmd("USER", "root")
    )
  )
  .dependsOn(core, auth)
  .aggregate(core, auth)

scalacOptions ++= Seq("-feature",  "-language:postfixOps", "-language:reflectiveCalls")

routesGenerator := InjectedRoutesGenerator