name := "pippin-core"
libraryDependencies ++= Seq(
	"com.github.tototoshi" %% "scala-csv" % "1.3.10"
) map (_.exclude("xml-apis", "xml-apis"))
