package org.qq.data
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.actor._
import akka.event.LoggingReceive
import org.qq.common._
/**
  * Created by Scott on 3/21/16.
  */
class ESactor(es:ES) extends Actor{
  def receive = {
    case Shuoshuo_data(qq,json) => es.index("qq","shuoshuo",json,qq)
  }
}
