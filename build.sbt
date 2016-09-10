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
		aggregate(core, core, db_load)
