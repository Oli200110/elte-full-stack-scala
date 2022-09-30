import sbt._

object Dependencies {
  val coreDependencies = Seq(
    "org.scala-lang" % "scala-library" % "2.13.9",
    "org.scala-lang" % "scala-reflect" % "2.13.9",
    "org.scala-lang.modules" %% "scala-collection-contrib" % "0.2.2",
    "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
    "org.scalatest" %% "scalatest" % "3.2.12" % "test",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
  )
}