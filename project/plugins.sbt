addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.4")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")
addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.5.0")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.8.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.22")

unmanagedSourceDirectories in Compile += {
  baseDirectory.value.getParentFile / "sbt-shared"
}

// addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.5.0-RC2")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.12.0")
