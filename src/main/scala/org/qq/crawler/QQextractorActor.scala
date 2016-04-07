package org.qq.crawler

import akka.actor.{ActorRef, Actor}
import org.qq.common.{Bloomfilter, Target}
import spray.json.JsObject

/**
  * Created by Scott on 3/28/16.
  */
class QQextractorActor(target_receiver:ActorRef) extends Actor with QQextractor{
  val size = 500000000
  val hash_num = 5
  val filter = Bloomfilter[String](size,hash_num) //if not in it return True, otherwise False
  def receive = {
    case js:String => fetchQQ(js).foreach(target_receiver ! Target(_))
  }
}

trait QQextractor{
  final val qq_reg = """"uin":(\d+)""".r
  def filter:(String => Boolean)
  def fetchQQ(js:String):List[String] = {
    qq_reg.findAllMatchIn(js)
      .map(mat => mat.group(1))
      .filterNot(filter(_))
      .toList
  }
}
