import com.goshoplane.sbt.standard.libraries.StandardLibraries._

name := "higgs-auth"

scalaVersion := Version.scala

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  "com.goshoplane"    %% "neutrino-core" % "0.0.1",
  "com.goshoplane"    %% "neutrino-auth" % "0.0.1",
  "org.bitbucket.b_c" % "jose4j" % "0.4.1"
) ++ Libs.akka ++ Libs.commonsCore ++ Libs.scalaz