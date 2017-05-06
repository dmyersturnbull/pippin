name := "kl-common-scala-core"
libraryDependencies ++= Seq(
	"org.scala-lang.modules" %% "scala-xml" % "1.0.6",
	"com.github.tototoshi" %% "scala-csv" % "1.3.4"
) map (_.exclude("xml-apis", "xml-apis"))
