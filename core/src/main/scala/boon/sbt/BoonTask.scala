package boon.sbt

import sbt.testing.Task
import sbt.testing.TaskDef
import sbt.testing.EventHandler
import sbt.testing.Logger
import sbt.testing.Status

import boon.Boon
import boon.model.SuiteState
import boon.printers.ColourOutput
import boon.result.SuiteOutput
import boon.model.SuiteResult

import boon.sbt.Event.createEvent
import boon.sbt.Event.createErrorEvent
import boon.sbt.Event.handleEvent
import boon.sbt.SuiteLoader.loadSuite

import scala.util.Failure
import scala.util.Success
import scala.util.Try

final class BoonTask(val taskDef: TaskDef,
                         cl: ClassLoader,
                         printer: (SuiteOutput, ColourOutput, String => Unit) => Unit,
                         statusLister: TestStatusListener) extends Task {

  def tags(): Array[String] = Array.empty

  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {
      val suiteTry = loadSuite(taskDef.fullyQualifiedName, cl)
      val startTime = System.currentTimeMillis()
      val suiteResultTry = suiteTry.flatMap[SuiteResult](suite => Try(Boon.runSuiteLike(suite)))
      val endTime = System.currentTimeMillis()
      val timeTaken = endTime - startTime

      suiteResultTry match {
        case Failure(error) =>
          handleEvent(createErrorEvent(taskDef, error, timeTaken), eventHandler)
          statusLister.suiteFailed(error.getMessage)
          logError(s"could not load class: ${taskDef.fullyQualifiedName}", error, loggers)
        case Success(suiteResult) =>
          handleEvent(
            createEvent[SuiteResult](taskDef, suiteResultToStatus, suiteResult, timeTaken), eventHandler)
          statusLister.suiteResult(suiteResult)
          logResult(SuiteOutput.toSuiteOutput(suiteResult), loggers)
      }

      Array.empty
  }

  private def suiteResultToStatus(sr: SuiteResult): Status =
    SuiteResult.suiteResultToSuiteState(sr) match {
      case SuiteState.Passed => Status.Success
      case SuiteState.Failed => Status.Failure
    }


  private def logResult(suiteOutput: SuiteOutput, loggers: Array[Logger]): Unit = {
    loggers.foreach { log =>
      printer(suiteOutput, ColourOutput.fromBoolean(log.ansiCodesSupported), log.info(_))
    }
  }

  private def logError(message: String, error: Throwable, loggers: Array[Logger]): Unit = {
    loggers.foreach{ l =>
      l.error(message)
      l.trace(error)
    }
  }
}