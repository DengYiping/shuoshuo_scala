package org.qq.parser

import akka.actor.Actor
import spray.json.JsObject
import org.qq.common._
/**
  * Created by Scott on 3/21/16.
  */
class Parser extends Actor{
  val bloom_filter = Bloomfilter[String](500000000,5)
  val qq_reg = """"uin":(\d+)""".r

  def receive = {
    case SsResponse(qq,js) => try{
      //parse shuoshuo
      val parsed = shuoshuoParser(js)
      sender() ! ParsedResponse(qq,parsed)
      //find qq number
      qq_reg.findAllMatchIn(js.compactPrint)
        .map(mat => mat.group(1))
        .filterNot(bloom_filter.contains(_))
        .foreach(sender() ! Target(_))
    }catch {case _ =>}
  }
}
