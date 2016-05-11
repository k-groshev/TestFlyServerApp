package actors

import akka.actor.{Actor, Props}

import scala.collection.immutable.ListMap
import scala.collection.mutable

case class StatisticsUnit(avgTime: Double, count: Int, countParseError: Int)

case class Statistics(results: Array[StatisticsUnit], cnt: Int) {
  val n = results.length
}

object ShowStatisticsMessage

object AggregationActor {
  def props = Props[AggregationActor]
}

class AggregationActor extends Actor {

  val res = mutable.Map.empty[Long,List[EndRequestMessage]]
  var cnt = 0

  def getStatistics:Statistics = {

    def avgTimeRequest = ListMap(res.toSeq.sortBy(_._1):_*).values
      .map(v => v.foldLeft((0,0,0D))((b,a) => (if (a.parseCheck) (b._1+1,b._2,b._3 + a.duration) else (b._1,b._2+1,b._3))))
      .map(a => StatisticsUnit(avgTime = a._3/a._1,count = a._1,countParseError = a._2))

    Statistics(avgTimeRequest.toArray,cnt)
  }

  def addResult(endRequest: EndRequestMessage) = {
    val second = endRequest.start.getEpochSecond

    res(second) = res.get(second) match {
      case Some(l) => endRequest::l
      case None => endRequest::Nil
    }

    cnt += 1
  }

  override def receive: Receive = {
    case endRequest:EndRequestMessage => addResult(endRequest)
    case ShowStatisticsMessage => sender ! getStatistics
  }
}
