package com.olegych.scastie.sbtscastie

import com.olegych.scastie.api.{
  ConsoleOutput,
  ProcessOutput,
  ProcessOutputType,
  RuntimeError,
  RuntimeErrorWrap
}

import sbt._
import Keys._

import sbt.internal.LogManager
import play.api.libs.json.Json
import org.apache.logging.log4j.core
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.layout.PatternLayout
import org.apache.logging.log4j.message.ObjectMessage
import sbt.internal.util.{ObjectEvent, StringEvent, TraceEvent}

import org.apache.logging.log4j.core.appender.AbstractAppender

import java.io.{PrintWriter, OutputStream}

object RuntimeErrorLogger {
  private object NoOp {
    def apply(): NoOp = {
      def out(in: String): Unit = {
        println(
          Json.stringify(
            Json.toJson(
              ConsoleOutput
                .SbtOutput(ProcessOutput(in.trim, ProcessOutputType.StdOut))
            )
          )
        )
      }

      new NoOp(new OutputStream {
        override def close(): Unit = ()
        override def flush(): Unit = ()
        override def write(b: Array[Byte]): Unit =
          out(new String(b))
        override def write(b: Array[Byte], off: Int, len: Int): Unit =
          out(new String(b, off, len))
        override def write(b: Int): Unit = ()
      })
    }
  }
  private class NoOp(os: OutputStream) extends PrintWriter(os)

  val settings: Seq[sbt.Def.Setting[_]] = Seq(
    extraLoggers := {
      val clientLogger = new AbstractAppender(
        "FakeAppender",
        null,
        PatternLayout.createDefaultLayout()
      ) {
        override def append(event: core.LogEvent): Unit = {}
      }
      clientLogger.start()
      // val currentFunction = extraLoggers.value
      (key: ScopedKey[_]) =>
        Seq(clientLogger)
    },
    showSuccess := false,
    logManager := LogManager.defaults(
      extraLoggers.value,
      ConsoleOut.printWriterOut(NoOp())
    )
  )
}
