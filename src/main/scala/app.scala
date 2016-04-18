/**
 * Created by nperez on 4/18/16.
 */

package STSlib

import akka.actor.{Props, ActorSystem}

object app {
  def main(args: Array[String]) {

    val numberOfSensors = args(0).toInt

    val system = ActorSystem("sensor")

    val socket = system.actorOf(Props[SocketActor], "SocketActor")

    socket ! StartConnection(9091)

    (1 to numberOfSensors).foreach { i =>
      val generator = system.actorOf(Sensor.props(i), s"Sensor:$i")
      generator ! StartMsg("hello")
    }

    io.StdIn.readLine("Press any key to stop")
  }
}
