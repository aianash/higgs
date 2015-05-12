name := """higgs"""

version := "0.0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.6",
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "com.goshoplane"    %% "commons-catalogue" % "0.0.1",
  "com.twitter"       %% "finagle-thrift" % "6.24.0",
  "org.apache.thrift" % "libthrift" % "0.9.2",
  "com.twitter"       %% "bijection-core" % "0.6.2",
  "com.twitter"       %% "bijection-util" % "0.6.2",
  "org.bitbucket.b_c" % "jose4j" % "0.4.1",
  "com.restfb"        % "restfb" % "1.10.1"
)

scalacOptions ++= Seq("-feature",  "-language:postfixOps")