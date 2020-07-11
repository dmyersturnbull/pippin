name := "pippin"

description := "Common Scala utilities"

lazy val commonSettings = Seq(
	organization := "com.github.dmyersturnbull",
	organizationHomepage := Some(url("https://github.com/dmyersturnbull")),
	version := "0.6.0-SNAPSHOT",
	isSnapshot := true,
	scalaVersion := "2.13.3",
	publishMavenStyle := true,
	publishTo :=
		Some(if (isSnapshot.value)
			"Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
			else "Sonatype Releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
		),
	publishArtifact in Test := false,
	pomIncludeRepository := { _ => false },
	javacOptions ++= Seq("-source", "14", "-target", "14", "-Xlint:all"),
	scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:postfixOps"),
	testOptions in Test += Tests.Argument("-oF"),
	homepage := Some(url("https://github.com/dmyersturnbull/pippin")),
	licenses := Seq("Apache Software License, Version 2.0"  -> url("https://www.apache.org/licenses/LICENSE-2.0")),
	developers := List(Developer("dmyersturnbull", "Douglas Myers-Turnbull", "---", url("https://github.com/dmyersturnbull"))),
	startYear := Some(2016),
	scmInfo := Some(ScmInfo(url("https://github.com/dmyersturnbull/pippin"), "https://github.com/dmyersturnbull/pippin.git")),
	libraryDependencies ++= Seq(
		"com.typesafe" % "config" % "1.4.0",
		"com.google.guava" % "guava" % "29.0-jre",
		"org.slf4j" % "slf4j-api" % "2.0.0-alpha1",
		"com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
		"org.scalatest" %% "scalatest" % "3.2.0" % "test",
		"org.scalactic" %% "scalactic" % "3.2.0" % "test",
		"org.scalacheck" %% "scalacheck" % "1.14.3" % "test",
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

lazy val root = (project in file(".")).
		settings(commonSettings: _*).
		aggregate(core, logconfig, grammars, misc)
