import com.typesafe.sbt.SbtStartScript

name := "higgs-integration-tests"

resolvers += "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"

libraryDependencies ++= Seq(
  ws,
  "me.lessis" %% "lapse" % "0.1.0"
)

seq(SbtStartScript.startScriptForClassesSettings: _*)