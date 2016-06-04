package app

import java.io.{BufferedWriter, File, FileWriter}
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, LocalDateTime}

import actors._
import akka.actor.{ActorSystem, Props}
import akka.pattern._
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory
import utl.{CSVWriter, FileUtl}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object TestApp extends App {

  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def start = {
    val system = ActorSystem("TestingSystem")
    val resultReceiver = system.actorOf(AggregationActor.props)

    def printStatistics(stat: Statistics) = {
      logger.info(s"-------- cnt: ${stat.cnt}---------")
      stat.results.foreach(r => logger.info(s"avgTime = ${r.avgTime} ms; count = ${r.count}; countParseError = ${r.countParseError}"))

      CSVWriter.printStatisticsToFile(stat)
    }

    def drawGraph(stat: Statistics) = {
      import com.quantifind.charts.Highcharts._

      hold
      line(stat.results.zipWithIndex.map(a=>(a._2,a._1.count)).toIndexedSeq)
      xAxis("second")
      yAxis("request/second")
      legend(Seq("Request per second"))

      unhold
      line(stat.results.zipWithIndex.map(a=>(a._2,a._1.avgTime)).toIndexedSeq)
      xAxis("second")
      yAxis("avgTime(ms)")
      legend(Seq("Average time"))

      unhold
      line(stat.results.zipWithIndex.map(a=>(a._2,a._1.countParseError)).toIndexedSeq)
      xAxis("second")
      yAxis("error/second")
      legend(Seq("Error per second"))

    }


    def runWithCheck(optLast: Option[Statistics] = None):Unit = {
      Thread.sleep(2000)

      val f = (resultReceiver ? ShowStatisticsMessage)(60 seconds).mapTo[Statistics]

      f.onComplete(a => a match {

        case Success(stat) =>

          printStatistics(stat)

          val continue = optLast match {
            case Some(lastStat) => lastStat.results.length != stat.results.length
            case None => true
          }

          if (continue)
            runWithCheck(Some(stat))
          else {
            drawGraph(stat)
            system.terminate()
            logger.info("Stop.")
          }

        case Failure(e) =>
          logger.error(s"error !!!! $e")
      })

    }


    FileUtl.read
      .foreach(path => {
        val actor = system.actorOf(FileWorkerActor.props(resultReceiver))
        actor ! StartMessage(path)
      })

    runWithCheck()
  }

  start

}
