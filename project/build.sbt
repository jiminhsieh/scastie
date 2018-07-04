import SbtShared.{apiProject, currentScalaVersion}

val api212 = apiProject(currentScalaVersion, fromSbt = true)
lazy val api212JVM = api212.jvm

libraryDependencies += "com.typesafe" % "config" % "1.3.1"

dependsOn(api212JVM)
