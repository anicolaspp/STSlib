/**
 * Created by nperez on 4/18/16.
 */

import java.io.PrintStream
import java.net.{ServerSocket, SocketAddress, Socket}

import akka.actor._
import scala.concurrent.ExecutionContext.Implicits._

import scala.util.Random

case class SData(sid: Int, temp: Double, time: Int)

class Sensor(id: Int)  extends Actor with ActorLogging {

  val __tick__ = "__TICK__"

  def receive = {
    case StartMsg(m)             =>  run
    case __tick__        =>  {
      val data = getData()
      log.info(s"Data to be sent: ${data.temp}")

      val streamerActor = context.actorSelection("/user/SocketActor")

      streamerActor ! data
    }
  }

  def getData() = {
    val value = Random.nextDouble()

    log.info("Generating temparature: {}", value)
    SData(id, value, 1)
  }

  def run() = {
    import scala.concurrent.duration._

    context.system.scheduler.schedule(1 seconds, 100 milliseconds, self, __tick__)
  }
}

object Sensor {
  def props(id: Int): Props = Props(new Sensor(id))
}

case class StartMsg(msg: String)
case class StartConnection(port: Int)

class SocketActor extends Actor with ActorLogging {

  var connectionPool = List[(Int, Socket)]()

  var outSocket: Option[Socket] = None

  def openConnection(port: Int) = {

    log.info("Openning connection")

    val socket = new ServerSocket(port)


    log.info("waitig for connections")
    val s = socket.accept()

    log.info(s"Connected at: ${s.getInetAddress}")
    outSocket = Some(s)
  }

  def receive = {
    case StartConnection(port)  => openConnection(port)

    case data @ SData(sid, temp, time) => outSocket match {
      case None =>  {
        openConnection(9090)
        self ! data
      }

      case Some(s)  =>  {

        println(data)

        val out = new PrintStream(s.getOutputStream())

        out.print(data)
        out.println()

        out.flush()
      //  out.close()
      }
    }
  }
}

