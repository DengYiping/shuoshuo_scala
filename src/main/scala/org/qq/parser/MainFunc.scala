package org.qq.parser

import akka.actor.{Props, ActorSystem}
import org.qq.login.QQ
import org.qq.common._
/**
  * Created by Scott on 3/21/16.
  */
object MainFunc extends App {
  implicit val system = ActorSystem("crawler")
  val skey = "@Huwran0p0"
  val handler = system.actorOf(Props(new ResponseHandler( QQ(649899819L, skey) )),"ResponseHandler")
  handler ! Target("2304808039")
}
=