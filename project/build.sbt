import SbtShared.{apiProject, currentScalaVersion}

val apiCurrent = apiProject(currentScalaVersion, fromSbt = true)
lazy val apiJVM = apiCurrent.jvm

libraryDependencies += "com.typesafe" % "config" % "1.3.1"

dependsOn(apiJVM)
