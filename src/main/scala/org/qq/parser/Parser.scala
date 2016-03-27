package org.qq.parser

import akka.actor.Actor
import spray.json._
import DefaultJsonProtocol._
import org.qq.common._
/**
  * Created by Scott on 3/21/16.
  */
class Parser extends Actor{
  val bloom_filter = Bloomfilter[String](500000000,5)
  val qq_reg = """"uin":(\d+)""".r

  def receive = {
    case ShuoShuoJsResponse(qq,js) => try{
      //parse shuoshuo
      shuoshuoParser(js).foreach(m =>sender() ! Json_doc(id = m._1,data = m._2.toJson.compactPrint))
      //find qq number
      qq_reg.findAllMatchIn(js.compactPrint)
        .map(mat => mat.group(1))
        .filterNot(bloom_filter.contains(_))
        .foreach(sender() ! Target(_))
    }catch {case _:Throwable =>}
  }
}
