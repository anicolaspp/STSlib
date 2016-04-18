/**
 * Created by nperez on 4/18/16.
 */

package STSlib

import akka.actor._

import scala.concurrent.ExecutionContext.Implicits._
import scala.util.Random

class Sensor(id: Int)  extends Actor with ActorLogging {

  val __tick__ = "__TICK__"
  var scheduler: Cancellable = _

  def receive = {
    case StartMsg(m)             =>  run
    case __tick__        =>  {
      val data = getData()
      log.info(s"Data to be sent: ${data.temp}")

      val streamerActor = context.actorSelection("/user/SocketActor")

      streamerActor ! data
    }
    case StopMsg          => {
      scheduler.cancel()
    }
  }

  def getData() = {
    val value = Random.nextDouble()

    log.info("Generating temparature: {}", value)
    SData(id, value, 1)
  }

  def run() = {
    import scala.concurrent.duration._

    scheduler = context.system.scheduler.schedule(1 seconds, 100 milliseconds, self, __tick__)
  }
}

object Sensor {
  def props(id: Int): Props = Props(new Sensor(id))
}





