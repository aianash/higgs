name := "higgs-search"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.6",
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "com.goshoplane"    %% "neutrino-core" % "0.0.1",
  "com.goshoplane" %% "creed-core" % "1.0.0"
)