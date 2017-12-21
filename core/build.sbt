name := "skale-core"
libraryDependencies ++= Seq(
	"org.scala-lang.modules" %% "scala-xml" % "1.0.6",
	"com.github.tototoshi" %% "scala-csv" % "1.3.5"
) map (_.exclude("xml-apis", "xml-apis"))
