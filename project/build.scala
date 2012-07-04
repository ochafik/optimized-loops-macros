import sbt._
import Keys._

object OptimizedLoopsBuild extends Build 
{
  lazy val infoSettings = Seq(
      organization := "com.nativelibs4java",
      version := "0.1-SNAPSHOT",
      licenses := Seq("BSD-3-Clause" -> url("http://www.opensource.org/licenses/BSD-3-Clause")),
      homepage := Some(url("https://github.com/ochafik/optimized-loops-macros"))
  )
  lazy val mavenSettings = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("-SNAPSHOT")) 
        Some("snapshots" at nexus + "content/repositories/snapshots") 
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <scm>
        <url>git@github.com:ochafik/optimized-loops-macros.git</url>
        <connection>scm:git:git@github.com:ochafik/optimized-loops-macros.git</connection>
      </scm>
      <developers>
        <developer>
          <id>ochafik</id>
          <name>Olivier Chafik</name>
          <url>http://ochafik.com</url>
        </developer>
      </developers>
    )
  )
  
  lazy val scalaInlinable = 
    Project(
      id = "scala-inlinable",
      base = file("."), 
      settings = 
        Defaults.defaultSettings ++ 
        infoSettings ++
        mavenSettings ++ 
        Seq(
          scalaVersion := "2.10.0-M4",
          
          libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _),
          libraryDependencies += "com.novocode" % "junit-interface" % "0.8" % "test",
          
          //scalacOptions += "-Xprint:typer",
          //scalacOptions += "-Yshow-trees", 
          
          javacOptions ++= Seq("-Xlint:unchecked"),
          scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
          scalacOptions ++= Seq("-language:experimental.macros")
        )
    ) 
}
