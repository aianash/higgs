name := "higgs-auth"

scalaVersion := "2.11.7"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.12",
  "com.typesafe.akka" %% "akka-actor" % "2.3.12",
  "com.goshoplane"    %% "commons-core" % "0.1.0",
  "com.goshoplane"    %% "neutrino-core" % "0.0.1",
  "com.goshoplane"    %% "neutrino-auth" % "0.0.1",
  "org.bitbucket.b_c" % "jose4j" % "0.4.1",
  "org.scalaz" %% "scalaz-core" % "7.1.1"
)