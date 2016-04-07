package org.qq.main

import akka.actor._
import org.qq.login.QQ
import org.qq.main.toporder.CrawlerStart

/**
  * Created by Scott on 3/31/16.
  */
object Boot extends App {
  val system = ActorSystem("ShuoshuoCrawlerSystem")
  val superviser = system.actorOf(Props[SuperviserActor],"superviser")
}
