package com.olegych.scastie

import java.io.File
import akka.event.LoggingAdapter

/**
  */
class RendererTemplate(dir: File, log: LoggingAdapter, uniqueId: String) {
  def create = {
    log.info("creating paste sbt project")
    val out = for {
      sbt <- resource.managed(new Sbt(new File("renderer-template"), log))
    } yield {
      val path = dir.getAbsolutePath.replaceAll("\\\\", "/")
      sbt.process(
        """set G8Keys.outputPath in G8Keys.g8 in Compile := file("%s") """.format(path)) ++
          sbt.process( """set G8Keys.properties in G8Keys.g8 in Compile := Map(("uniqueId", "%s")) """
              .format(uniqueId)) ++
          sbt.process( """g8""")
    }
    out.either match {
      case Left(errors) => errors.foreach(log.error(_, "Exception creating template")); throw errors.head
      case Right(out) => out
    }
    log.info("starting sbt")
    new Sbt(dir, log, uniqueId)
  }
}