name := "ShapelessBenchmark"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.2"
)
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.8"