/**
  * Created by nperez on 4/18/16.
 */

package STSlib


case class SData(sid: Int, temp: Double, time: Int)
case class StartMsg(msg: String)
case class StartConnection(port: Int)
case object StopMsg
