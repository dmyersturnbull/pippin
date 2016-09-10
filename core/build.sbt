libraryDependencies ++= Seq(
	"com.typesafe" % "config" % "1.3.0",
	"com.jsuereth" %% "scala-arm" % "1.4",
	"com.github.tototoshi" %% "scala-csv" % "1.3.3",
	"com.google.guava" % "guava" % "19.0",
	"org.slf4j" % "slf4j-api" % "1.7.21",
	"com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
	"ch.qos.logback" %  "logback-classic" % "1.1.7",
	"org.mockito" % "mockito-core" % "2.0.101-beta" % "test",
	"org.scalatest" %% "scalatest" % "3.0.0" % "test",
	"org.scalactic" %% "scalactic" % "3.0.0" % "test",
	"org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
) map (_.exclude("log4j", "log4j")) map (_.exclude("org.slf4j", "slf4j-log4j12"))