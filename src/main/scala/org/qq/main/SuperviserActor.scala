package org.qq.main

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.io.IO
import akka.util.Timeout
import org.qq.common.ESCount
import org.qq.crawler.CrawlerActor
import org.qq.data.{ESactor, ES}
import org.qq.login.QQ
import org.qq.web.MainServiceActor
import spray.can.Http
import toporder._
import scala.concurrent.duration._
import akka.pattern.ask

/**
  * Created by Scott on 3/30/16.
  */
class SuperviserActor extends Actor with ActorLogging{
  override val supervisorStrategy= OneForOneStrategy() {
    case _:Throwable => Restart
  }
  val es = ES()
  val es_actor = context.actorOf(Props(new ESactor(es)),"ElasticSearchActor")
  val crawler = context.actorOf(Props(new CrawlerActor(es_actor)),"CrawlerActor")
  val service=context.actorOf(Props(new MainServiceActor(crawler,es_actor,es)),"WebService")
  var es_count = 0L;
  implicit val timeout=Timeout(5.seconds)
  implicit val system = context.system
  IO(Http) ? Http.Bind(service,interface="localhost",port=8080)
  def receive = {
    case x:CrawlerStart => crawler ! x
    case ESclean => es_actor ! ESclean
    case ESCount(c) => es_count = c
    case "Count" => sender() ! es_count
  }
}




