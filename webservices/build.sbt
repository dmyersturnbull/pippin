libraryDependencies ++= Seq(
	"org.apache.axis2" % "axis2" % "1.7.3" pomOnly(), // apparently needed for ChemSpider API
	"org.apache.axis2" % "axis2-kernel" % "1.7.3", // apparently needed for ChemSpider API
	"org.apache.axis2" % "axis2-adb" % "1.7.3", // apparently needed for ChemSpider API
	"org.apache.axis2" % "axis2-transport-local" % "1.7.3", // apparently needed for ChemSpider API
	"org.apache.axis2" % "axis2-transport-http" % "1.7.3", // apparently needed for ChemSpider API
	"org.apache.axis2" % "axis2-jaxws" % "1.7.3" // apparently needed for ChemSpider API
) map (_.exclude("log4j", "log4j")) map (_.exclude("org.slf4j", "slf4j-log4j12")) map (_.exclude("org.apache.ws.commons.axiom", "axiom-impl"))
