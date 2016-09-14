name := "kokel-utils-misc"
libraryDependencies ++= Seq(
	"com.google.zxing" % "core" % "3.2.1",
	"com.google.zxing" % "javase" % "3.2.1"
) map (_.exclude("log4j", "log4j")) map (_.exclude("org.slf4j", "slf4j-log4j12"))
assemblyJarName in assembly := "kokel-utils-misc.jar"
