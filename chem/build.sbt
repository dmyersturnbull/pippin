name := "skale-chem"
// depressingly, CDK uses log4j directly; also, I don't know which of these I need
libraryDependencies ++= Seq(
	"org.openscience.cdk" % "cdk-standard" % "2.1.1",
	"org.openscience.cdk" % "cdk-interfaces" % "2.1.1",
	"org.openscience.cdk" % "cdk-core" % "2.1.1",
	"org.openscience.cdk" % "cdk-silent" % "2.1.1",
	"org.openscience.cdk" % "cdk-diff" % "2.1.1",
	"org.openscience.cdk" % "cdk-smiles" % "2.1.1",
	"org.openscience.cdk" % "cdk-inchi" % "2.1.1",
	"org.openscience.cdk" % "cdk-io" % "2.1.1",
	"org.openscience.cdk" % "cdk-diff" % "2.1.1",
	"org.openscience.cdk" % "cdk-formula" % "2.1.1",
	"org.openscience.cdk" % "cdk-fingerprint" % "2.1.1",
	"org.openscience.cdk" % "cdk-extra" % "2.1.1",
	"org.openscience.cdk" % "cdk-charges" % "2.1.1"
) map (_.exclude("xml-apis", "xml-apis"))
