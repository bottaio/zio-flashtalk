name := "flashtalk-zio"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions += "-Ypartial-unification"

libraryDependencies += "org.scalaz" %% "scalaz-zio" % "1.0-RC1"
libraryDependencies += "org.scalaz" %% "scalaz-zio-interop-cats" % "1.0-RC1"
libraryDependencies += "org.scalaz" %% "scalaz-zio-interop-scalaz7x" % "1.0-RC1"

val http4sVersion = "0.19.0"
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

val circeVersion = "0.10.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies += "com.lihaoyi" %% "requests" % "0.1.7"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.27"
