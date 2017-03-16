enablePlugins(OrnatePlugin)

name := "ornate-scalafiddle"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.novocode" %% "ornate" % "0.4",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "org.json4s" %% "json4s-native" % "3.5.0"
)

(dependencyClasspath in Ornate) := (fullClasspath in Runtime).value ++ (dependencyClasspath in Ornate).value