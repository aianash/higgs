name := """higgs"""

version := "0.0.1"

lazy val common = (project in file("modules/common"))

lazy val auth = (project in file("modules/auth")).enablePlugins(PlayScala).dependsOn(common)

lazy val bucket = (project in file("modules/bucket")).enablePlugins(PlayScala).dependsOn(common, auth)

lazy val feed = (project in file("modules/feed")).enablePlugins(PlayScala).dependsOn(common, auth)

lazy val search = (project in file("modules/search")).enablePlugins(PlayScala).dependsOn(common, auth, shopplan)

lazy val shopplan = (project in file("modules/shopplan")).enablePlugins(PlayScala).dependsOn(common, auth)

lazy val user = (project in file("modules/user")).enablePlugins(PlayScala).dependsOn(common, auth)

lazy val integrationTest = (project in file("integration_test")).dependsOn(common)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .dependsOn(common, auth, bucket, feed, search, shopplan, user)
  .aggregate(common, auth, bucket, feed, search, shopplan, user, integrationTest)

scalacOptions ++= Seq("-feature",  "-language:postfixOps", "-language:reflectiveCalls")