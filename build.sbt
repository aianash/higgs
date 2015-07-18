import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd, CmdLike}

name := """higgs"""

version := "0.0.1"

lazy val common = (project in file("modules/common")).disablePlugins(DockerPlugin)

lazy val auth = (project in file("modules/auth")).enablePlugins(PlayScala).disablePlugins(DockerPlugin).dependsOn(common)

lazy val bucket = (project in file("modules/bucket")).enablePlugins(PlayScala).disablePlugins(DockerPlugin).dependsOn(common, auth)

lazy val feed = (project in file("modules/feed")).enablePlugins(PlayScala).disablePlugins(DockerPlugin).dependsOn(common, auth)

lazy val search = (project in file("modules/search")).enablePlugins(PlayScala).disablePlugins(DockerPlugin).dependsOn(common, auth, shopplan)

lazy val shopplan = (project in file("modules/shopplan")).enablePlugins(PlayScala).disablePlugins(DockerPlugin).dependsOn(common, auth)

lazy val user = (project in file("modules/user")).enablePlugins(PlayScala).disablePlugins(DockerPlugin).dependsOn(common, auth)

lazy val integrationTest = (project in file("integration_test")).disablePlugins(DockerPlugin).dependsOn(common)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(JavaAppPackaging)
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
  .dependsOn(common, auth, bucket, feed, search, shopplan, user)
  .aggregate(common, auth, bucket, feed, search, shopplan, user, integrationTest)

scalacOptions ++= Seq("-feature",  "-language:postfixOps", "-language:reflectiveCalls")