package org.qq.parser

import akka.actor.{Props, ActorSystem}
import org.qq.login.QQ
import org.qq.common._
/**
  * Created by Scott on 3/21/16.
  */
object MainFunc extends App {
  implicit val system = ActorSystem("crawler")
  val handler = system.actorOf(Props(new ResponseHandler( QQ(649899819L, "@WjWqnyHda"))),"ResponseHandler")
  handler ! Target("2304808039")
  val qq_list = "2304808039"::"649899819"::Nil
  qq_list.foreach(handler ! _)
}
