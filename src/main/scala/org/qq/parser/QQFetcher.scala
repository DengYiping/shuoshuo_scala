package org.qq.parser

import akka.actor.Actor
import org.qq.common.Target

/**
  * Created by Scott on 3/21/16.
  */
class QQFetcher extends Actor{
  val bloom_filter = Bloomfilter[String](500000000,5)
  val qq_reg = """"uin":(\d+)""".r
  def receive = {
    case json:String => {
      qq_reg.findAllMatchIn(json).map(mat => mat.group(1)).filterNot(bloom_filter.contains(_)).foreach(sender() ! Target(_))
    }
  }
}
