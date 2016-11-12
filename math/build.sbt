name := "kl-common-scala-math"
libraryDependencies ++= Seq(
	"org.scalanlp" %% "breeze" % "0.12",
	"com.squants"  %% "squants"  % "0.6.2"
)
assemblyJarName in assembly := "kl-common-scala-math.jar"
