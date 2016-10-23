name := """basics"""

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation", "-feature", "-language:implicitConversions")

// Change this to another test framework if you prefer
libraryDependencies ++=
  Seq(
    "org.typelevel" %% "cats" % "0.7.0" withSources
  )

