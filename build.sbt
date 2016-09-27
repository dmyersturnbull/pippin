name := "kokel-utils"

lazy val commonSettings = Seq(
	organization := "com.github.dmyersturnbull",
	version := "0.0.1",
	scalaVersion := "2.11.8",
	javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:all"),
	scalacOptions ++= Seq("-unchecked", "-deprecation"),
	testOptions in Test += Tests.Argument("-oF"),
	test in assembly := {},
	assemblyJarName in assembly := name.value + ".jar",
	target in assembly := file("target"),
	libraryDependencies ++= Seq(
		"com.typesafe" % "config" % "1.3.0",
		"com.jsuereth" %% "scala-arm" % "1.4",
		"com.google.guava" % "guava" % "19.0",
		"com.google.code.findbugs" % "jsr305" % "3.0.1", // to work around compiler warnings about missing anotations from Guava
		"org.slf4j" % "slf4j-api" % "1.7.21",
		"com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
		"ch.qos.logback" %  "logback-classic" % "1.1.7",
		"org.mockito" % "mockito-core" % "2.0.101-beta" % "test",
		"org.scalatest" %% "scalatest" % "3.0.0" % "test",
		"org.scalactic" %% "scalactic" % "3.0.0" % "test",
		"org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
	),
	pomExtra :=
			<url>https://github.com/dmyersturnbull/kokel-utils</url>
					<scm>
						<url>https://github.com/dmyersturnbull/kokel-utils</url>
						<connection>https://github.com/dmyersturnbull/kokel-utils.git</connection>
					</scm>
					<developers>
						<developer>
							<id>dmyersturnbull</id>
							<name>Douglas Myers-Turnbull</name>
							<url>https://www.dmyersturnbull.com</url>
							<timezone>-8</timezone>
						</developer>
					</developers>
					<issueManagement>
						<system>Github</system>
						<url>https://github.com/dmyersturnbull/kokel-utils/issues</url>
					</issueManagement>
)

lazy val core = project.
		settings(commonSettings: _*)

lazy val chem = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val webservices = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val math = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val misc = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val root = (project in file(".")).
		settings(commonSettings: _*).
		aggregate(core, chem, webservices, math, misc)
