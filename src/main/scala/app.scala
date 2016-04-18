/**
 * Created by nperez on 4/18/16.
 */

import akka.actor.{Props, ActorSystem}

object app {
  def main(args: Array[String]) {
    val system = ActorSystem("sensor")

    val socket = system.actorOf(Props[SocketActor], "SocketActor")

    socket ! StartConnection(9091)

    println(socket)


    (1 to 1000).foreach { i =>
      val generator = system.actorOf(Sensor.props(i), s"Sensor:$i")
      generator ! StartMsg("hello")
    }



    io.StdIn.readLine()
  }
}
