name := "skale-chem"
// depressingly, CDK uses log4j directly; also, I don't know which of these I need
libraryDependencies ++= Seq(
	"org.openscience.cdk" % "cdk-standard" % "1.5.15",
	"org.openscience.cdk" % "cdk-interfaces" % "1.5.15",
	"org.openscience.cdk" % "cdk-core" % "1.5.15",
	"org.openscience.cdk" % "cdk-silent" % "1.5.15",
	"org.openscience.cdk" % "cdk-diff" % "1.5.15",
	"org.openscience.cdk" % "cdk-smiles" % "1.5.15",
	"org.openscience.cdk" % "cdk-inchi" % "1.5.15",
	"org.openscience.cdk" % "cdk-io" % "1.5.15",
	"org.openscience.cdk" % "cdk-diff" % "1.5.15",
	"org.openscience.cdk" % "cdk-formula" % "1.5.15",
	"org.openscience.cdk" % "cdk-fingerprint" % "1.5.15",
	"org.openscience.cdk" % "cdk-extra" % "1.5.15",
	"org.openscience.cdk" % "cdk-charges" % "1.5.15"
) map (_.exclude("xml-apis", "xml-apis"))
