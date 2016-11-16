name := "kl-common-scala-misc"
libraryDependencies ++= Seq(
	"com.google.zxing" % "core" % "3.2.1",
	"com.google.zxing" % "javase" % "3.2.1",
	"com.googlecode.soundlibs" % "mp3spi" % "1.9.5-1"
)
assemblyJarName in assembly := "kl-common-scala-misc.jar"
