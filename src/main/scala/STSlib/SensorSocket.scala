/**
 * Created by nperez on 4/18/16.
 */
package STSlib

import java.io.PrintStream
import java.net.{ServerSocket, Socket}

import akka.actor.{ActorLogging, Actor}

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

    case StopMsg  =>  {
      outSocket = None
    }
  }
}