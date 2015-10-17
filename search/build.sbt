import com.goshoplane.sbt.standard.libraries.StandardLibraries._

name := "higgs-search"

scalaVersion := Version.scala

libraryDependencies ++= Seq(
  "com.goshoplane" %% "neutrino-core" % "0.0.1",
  "com.goshoplane" %% "creed-client" % "1.0.0",
  "com.goshoplane" %% "commons-owner" % "0.1.0"
) ++ Libs.akka