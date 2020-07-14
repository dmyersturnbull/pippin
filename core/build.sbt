name := "pippin-core"
libraryDependencies ++= Seq(
	"com.github.tototoshi" %% "scala-csv" % "1.3.6"
) map (_.exclude("xml-apis", "xml-apis"))
