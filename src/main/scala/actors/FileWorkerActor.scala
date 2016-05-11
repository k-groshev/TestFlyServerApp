package actors

import java.nio.file.Path

import akka.actor.{Actor, ActorRef, Props}
import utl.FileUtl

case class StartMessage(path: Path)

object FileWorkerActor {
  def props(resultReceiver: ActorRef) = Props(classOf[FileWorkerActor],resultReceiver)
}

class FileWorkerActor(resultReceiver: ActorRef) extends Actor {

  override def receive: Receive = {
    case StartMessage(path) =>
      val body = FileUtl.readEntity(path)

      if (!body.isEmpty) {
        val requestWorker = context.actorOf(RequestWorkerActor.props)
        requestWorker ! StartRequestMessage(body)
      }

    case endRequest:EndRequestMessage =>
      resultReceiver forward endRequest
  }
}

