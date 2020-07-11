name := "took-misc"
libraryDependencies ++= Seq(
	"org.tukaani" % "xz" % "1.8",
	"com.google.zxing" % "core" % "3.4.0",
	"com.google.zxing" % "javase" % "3.4.0",
	"com.googlecode.soundlibs" % "mp3spi" % "1.9.5.4",
	"org.openscience.cdk" % "cdk-standard" % "2.3",
	"org.openscience.cdk" % "cdk-interfaces" % "2.3",
	"org.openscience.cdk" % "cdk-core" % "2.3",
	"org.openscience.cdk" % "cdk-silent" % "2.3",
	"org.openscience.cdk" % "cdk-diff" % "2.3",
	"org.openscience.cdk" % "cdk-smiles" % "2.3",
	"org.openscience.cdk" % "cdk-inchi" % "2.3",
	"org.openscience.cdk" % "cdk-io" % "2.3",
	"org.openscience.cdk" % "cdk-diff" % "2.3",
	"org.openscience.cdk" % "cdk-formula" % "2.3",
	"org.openscience.cdk" % "cdk-fingerprint" % "2.3",
	"org.openscience.cdk" % "cdk-extra" % "2.3",
	"org.openscience.cdk" % "cdk-charges" % "2.3"
) map (_.exclude("xml-apis", "xml-apis"))
