name := "pippin-core"
libraryDependencies ++= Seq(
	"com.github.tototoshi" %% "scala-csv" % "1.3.6",
		"org.scalanlp" %% "breeze" % "2.1.0",
		"com.sksamuel.scrimage" %% "scrimage-core" % "4.0.32",
		"org.bytedeco" % "javacv-platform" % "1.5.7",
		"org.boofcv" % "core" % "0.41",
		"org.typelevel"  %% "squants"  % "1.8.3",
) map (_.exclude("xml-apis", "xml-apis"))
