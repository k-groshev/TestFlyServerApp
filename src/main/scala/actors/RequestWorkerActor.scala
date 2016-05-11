package actors

import java.time.{Duration, Instant}

import akka.actor.{Actor, Props}
import org.apache.http.{HttpEntity, HttpResponse}
import utl.{FileUtl, HttpUtl}

import scala.util.Try

case class StartRequestMessage(body: Array[Byte])

case class EndRequestMessage(start: Instant, duration: Long, response: Try[Array[Byte]], parseCheck: Boolean)

object RequestWorkerActor {
  def props = Props(classOf[RequestWorkerActor])
}

class RequestWorkerActor extends Actor {

  def checkResponse(body: Array[Byte]):Boolean =
    FileUtl.decompress(body).contains("array")

  override def receive: Receive = {
    case StartRequestMessage(body) =>
      val start = Instant.now
      val response = HttpUtl.execute(body)
      val duration = Duration.between(start,Instant.now).toMillis
      val parseCheck = response.map(r => checkResponse(r)).getOrElse(false)

      sender ! EndRequestMessage(start = start, duration = duration, response = response, parseCheck = parseCheck)
  }
}

