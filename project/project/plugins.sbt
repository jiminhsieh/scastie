addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.22")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.8.0")

unmanagedSourceDirectories in Compile += {
  baseDirectory.value.getParentFile.getParentFile / "sbt-shared"
}
