import com.goshoplane.sbt.standard.libraries.StandardLibraries._

name := "higgs-core"

scalaVersion := Version.scala

libraryDependencies ++= Seq(
  "com.goshoplane" %% "neutrino-core" % "0.0.1"
) ++ Libs.akka