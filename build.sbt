ThisBuild / scalaVersion := "3.2.0"

name := "pippin"

description := "Common Scala utilities"

lazy val commonSettings = Seq(
	organization := "com.github.dmyersturnbull",
	organizationHomepage := Some(url("https://github.com/dmyersturnbull")),
	version := "0.7.0-SNAPSHOT",
	isSnapshot := true,
	scalaVersion := "3.2.0",
	publishMavenStyle := true,
	publishTo :=
		Some(if (isSnapshot.value)
			"Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
			else "Sonatype Releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
		),
	publishArtifact in Test := false,
	pomIncludeRepository := { _ => false },
	javacOptions ++= Seq("-source", "18", "-target", "18", "-Xlint:all"),
	scalacOptions ++= Seq("-unchecked", "-deprecation"),
	homepage := Some(url("https://github.com/dmyersturnbull/pippin")),
	licenses := Seq("Apache Software License, Version 2.0"  -> url("https://www.apache.org/licenses/LICENSE-2.0")),
	developers := List(Developer("dmyersturnbull", "Douglas Myers-Turnbull", "---", url("https://github.com/dmyersturnbull"))),
	startYear := Some(2016),
	scmInfo := Some(ScmInfo(url("https://github.com/dmyersturnbull/pippin"), "https://github.com/dmyersturnbull/pippin.git")),
	libraryDependencies ++= Seq(
		"com.typesafe" % "config" % "1.4.2",
		"com.google.guava" % "guava" % "30.1.1-jre",
		"org.slf4j" % "slf4j-api" % "2.0.0",
		"com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
		"org.scalatest" %% "scalatest" % "3.2.13" % "test",
		"org.scalactic" %% "scalactic" % "3.2.13" % "test",
		"org.scalacheck" %% "scalacheck" % "1.16.0" % "test"
	),
	pomExtra :=
		<issueManagement>
			<system>Github</system>
			<url>https://github.com/dmyersturnbull/pippin/issues</url>
		</issueManagement>
)

lazy val core = project.
		settings(commonSettings: _*)

lazy val logconfig = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val grammars = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val misc = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val video = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val root = (project in file(".")).
		settings(commonSettings: _*).
		aggregate(core, logconfig, grammars, misc)
