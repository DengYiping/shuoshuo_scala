package org.qq.parser

import akka.actor.Actor
import spray.json.JsObject
import org.qq.common._
/**
  * Created by Scott on 3/21/16.
  */
class Parser extends Actor{
  def receive = {
    case SsResponse(qq,js) => try{
      val parsed = shuoshuoParser(js)
      sender() ! ParsedResponse(qq,parsed)
    }catch {case _ =>}
  }
}
