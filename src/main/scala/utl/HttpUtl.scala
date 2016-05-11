package utl

import app.Config._
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.client.HttpClientBuilder

import scala.util.{Failure, Try}

object HttpUtl {

  //  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def execute(body: Array[Byte]): Try[Array[Byte]] = {
    val client = HttpClientBuilder.create().build()

    val request = new HttpPost(url)
    request.setEntity(new ByteArrayEntity(body))
    val response = Try {
      val entity = client.execute(request).getEntity
      Stream.continually(entity.getContent.read()).takeWhile(_ != -1).map(_.toByte).toArray
    }

    client.close()
    response
  }

}
