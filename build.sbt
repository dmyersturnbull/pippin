name := "kl-common-scala"

description := "Common Scala utilities for the Kokel Lab"

lazy val commonSettings = Seq(
	organization := "com.github.kokellab",
	organizationHomepage := Some(url("https://github.com/kokellab")),
	version := "0.1.3-SNAPSHOT",
	isSnapshot := true,
	scalaVersion := "2.11.8",
	publishMavenStyle := true,
	publishTo :=
		Some(if (isSnapshot.value)
			"Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
			else "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
		),
	publishArtifact in Test := false,
	pomIncludeRepository := { _ => false },
	javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:all"),
	scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:postfixOps"),
	testOptions in Test += Tests.Argument("-oF"),
	homepage := Some(url("https://github.com/kokellab/kl-common-scala")),
	licenses := Seq("Apache Software License, Version 2.0"  -> url("https://www.apache.org/licenses/LICENSE-2.0")),
	developers := List(Developer("dmyersturnbull", "Douglas Myers-Turnbull", "dmyersturnbull@kokellab.com", url("https://github.com/dmyersturnbull"))),
	startYear := Some(2016),
	scmInfo := Some(ScmInfo(url("https://github.com/kokellab/kl-common-scala"), "https://github.com/kokellab/kl-common-scala.git")),
	libraryDependencies ++= Seq(
		"com.typesafe" % "config" % "1.3.0",
		"com.jsuereth" %% "scala-arm" % "1.4",
		"com.google.guava" % "guava" % "20.0",
		"com.google.code.findbugs" % "jsr305" % "3.0.1", // to work around compiler warnings about missing annotations from Guava
		"org.slf4j" % "slf4j-api" % "1.7.21",
		"com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
		"ch.qos.logback" %  "logback-classic" % "1.1.7",
		"org.scalatest" %% "scalatest" % "3.0.0" % "test",
		"org.scalactic" %% "scalactic" % "3.0.0" % "test",
		"org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
		"org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test",
		"com.storm-enroute" %% "scalameter-core" % "0.8.2" % "test"
	),
	pomExtra :=
		<issueManagement>
			<system>Github</system>
			<url>https://github.com/kokellab/kl-common-scala/issues</url>
		</issueManagement>
)

lazy val core = project.
		settings(commonSettings: _*)

lazy val logconfig = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val chem = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val webservices = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val math = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val grammars = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val misc = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val root = (project in file(".")).
		settings(commonSettings: _*).
		aggregate(core, logconfig, chem, webservices, math, grammars, misc)
