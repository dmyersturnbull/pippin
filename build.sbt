name := "skale"

description := "Common Scala utilities for the Kokel Lab"

lazy val commonSettings = Seq(
	organization := "com.github.kokellab",
	organizationHomepage := Some(url("https://github.com/kokellab")),
	version := "0.5.0-SNAPSHOT",
	isSnapshot := true,
	scalaVersion := "2.12.6",
	publishMavenStyle := true,
	publishTo :=
		Some(if (isSnapshot.value)
			"Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
			else "Sonatype Releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
		),
	publishArtifact in Test := false,
	pomIncludeRepository := { _ => false },
	javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:all"),
	scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:postfixOps"),
	testOptions in Test += Tests.Argument("-oF"),
	homepage := Some(url("https://github.com/kokellab/skale")),
	licenses := Seq("Apache Software License, Version 2.0"  -> url("https://www.apache.org/licenses/LICENSE-2.0")),
	developers := List(Developer("dmyersturnbull", "Douglas Myers-Turnbull", "dmyersturnbull@kokellab.com", url("https://github.com/dmyersturnbull"))),
	startYear := Some(2016),
	scmInfo := Some(ScmInfo(url("https://github.com/kokellab/skale"), "https://github.com/kokellab/skale.git")),
	libraryDependencies ++= Seq(
		"com.typesafe" % "config" % "1.3.3",
		"com.jsuereth" %% "scala-arm" % "2.0",
		"com.google.guava" % "guava" % "25.1-jre",
		"org.slf4j" % "slf4j-api" % "1.8.0-beta2",
		"com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
		"org.scalatest" %% "scalatest" % "3.0.5" % "test",
		"org.scalactic" %% "scalactic" % "3.0.5" % "test",
		"org.scalacheck" %% "scalacheck" % "1.14.0" % "test",
		"org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % "test"
	),
	pomExtra :=
		<issueManagement>
			<system>Github</system>
			<url>https://github.com/kokellab/skale/issues</url>
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
