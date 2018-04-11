package com.olegych.scastie.sbtscastie

import com.olegych.scastie.api.{ConsoleOutput, ProcessOutput, ProcessOutputType, RuntimeError, RuntimeErrorWrap}
import sbt._
import Keys._
import sbt.internal.LogManager
import play.api.libs.json.Json
import java.io.{OutputStream, PrintWriter}

import org.apache.logging.log4j.core
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.layout.PatternLayout
import org.apache.logging.log4j.message.ObjectMessage
import sbt.internal.util.{ObjectEvent, StringEvent, TraceEvent}

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
      /*val clientLogger = FullLogger {
        new Logger {
          def log(level: Level.Value, message: => String): Unit = ()

          def success(message: => String): Unit = () // << this is never called

          def trace(t: => Throwable): Unit = {

            // Nonzero exit code: 1
            val sbtTrap =
              t.isInstanceOf[RuntimeException] &&
                t.getMessage == "Nonzero exit code: 1" &&
                !t.getStackTrace.exists(
                  e => e.getClassName == "sbt.Run" && e.getMethodName == "invokeMain"
                )

            if (!sbtTrap) {
              val error = RuntimeErrorWrap(RuntimeError.fromThrowable(t))
              println(Json.stringify(Json.toJson(error)))
            }
          }
        }
      }*/

      val clientLogger = new AbstractAppender(
        "FakeAppender",
        null,
        PatternLayout.createDefaultLayout()
      ) {
              override def append(event: core.LogEvent): Unit = {
                val level = event.getLevel
                val message = event.getMessage

                println("### (Not Pattern yet) " + message.toString)

                message match {

                  case o: ObjectMessage => {
                    o.getParameter match {
                      case e: StringEvent => {
                        // console messages
                        println("### (StringEvent) " + e.message)
                      }
                      case e: ObjectEvent[_] => {
                        val error = RuntimeErrorWrap(RuntimeError.fromThrowable(e.message))
                        println(Json.stringify(Json.toJson(error)))
                        println("### (ObjectEvent)" + e.message)
                      }

                      case e: TraceEvent => {
                        println("### (TraceEvent)")
                        val error = RuntimeErrorWrap(RuntimeError.fromThrowable(e.message))
                        println(Json.stringify(Json.toJson(error)))
                      }
                      case e: Object => {
                        // log to server-site
                        println("### (Object)" +  e.toString)
                      }
                    }
                  }

                  case _ => {
                    println("### Other Type ###")
                    println("### " + level)
                    println("### " + message)
                  }
                }

              }
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
