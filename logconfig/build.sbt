name := "kl-common-scala-logconfig"

libraryDependencies := libraryDependencies.value map (_.exclude("org.slf4j", "slf4j-log4j12")) map (_.exclude("org.slf4j", "slf4j-log4j13")) map (_.exclude("org.slf4j", "slf4j-jdk14")) map (_.exclude("org.slf4j", "slf4j-simple")) map (_.exclude("org.slf4j", "slf4j-nop")) map (_.exclude("org.slf4j", "slf4j-jcl")) map (_.exclude("org.slf4j", "slf4j-nlog4j"))
