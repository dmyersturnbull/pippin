libraryDependencies ++= Seq(
	"org.scalanlp" %% "breeze" % "0.12",
	"com.squants"  %% "squants"  % "0.6.2"
) map (_.exclude("log4j", "log4j")) map (_.exclude("org.slf4j", "slf4j-log4j12"))