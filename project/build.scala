import sbt._
import Keys._

object OptimizedLoopsBuild extends Build 
{
  lazy val infoSettings = Seq(
      organization := "com.nativelibs4java",
      version := "1.0-SNAPSHOT",
      licenses := Seq("BSD-3-Clause" -> url("http://www.opensource.org/licenses/BSD-3-Clause")),
      homepage := Some(url("http://ochafik.com/blog/"))
  )
  lazy val mavenSettings = Seq(
    publishMavenStyle := true,
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("-SNAPSHOT")) 
        Some("snapshots" at nexus + "content/repositories/snapshots") 
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomIncludeRepository := { _ => false }
  )
  
  lazy val optimizedScalaLoops = 
    Project(
      id = "optimized-scala-loops",
      base = file("."), 
      settings = 
        Defaults.defaultSettings ++ 
        infoSettings ++
        mavenSettings ++ 
        Seq(
          scalaVersion := "2.10.0-M4",
          
          libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _),
          libraryDependencies += "com.novocode" % "junit-interface" % "0.8" % "test",
          
          javacOptions ++= Seq("-Xlint:unchecked"),
          scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
          scalacOptions ++= Seq("-language:experimental.macros")
        )
    ) 
}
