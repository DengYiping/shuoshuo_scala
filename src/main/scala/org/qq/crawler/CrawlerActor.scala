package org.qq.crawler

import akka.actor.SupervisorStrategy.{Stop, Restart}
import akka.actor._
import org.qq.login.QQ
import org.qq.common._
import org.qq.main.toporder._
/**
  * Created by Scott on 3/29/16.
  */

class CrawlerActor(es_actor:ActorRef) extends Actor with ActorLogging{
  val parser = context.actorOf(Props(new ParserActor(es_actor)),"parser")
  var is_started = false
  var requester:ActorRef = null
  var validator:ActorRef = null
  override val supervisorStrategy= OneForOneStrategy() {
    case _:Throwable => Restart
  }

  def receive ={
    case CrawlerStart(original_qq,seeds) => {
      if(!is_started){
        requester = context.actorOf(Props(new RequestRouter(parser,original_qq)),"requester")
        validator = context.actorOf(Props(new QQvalidator(original_qq,requester)),"validator")
        is_started = true
        seeds.foreach(seed => requester ! Target(seed))
        log.info("Crawler has successfully started.")
        sender() ! Succeed
      }
      else{
        log.error("Crawler has already started")
        sender() ! Failed
      }
    }
    case x:AddQQRequest => {
      if (is_started && requester != null && validator != null){
        validator ! x
        log.info("Successfully add worker qq:" + x.new_qq.qq.toString)
      }
    }
    case y:Target => {
      if (is_started && requester != null && validator != null){
        requester ! y
        log.info("Successfully add target:" + y.qq.toString)
      }
    }
    case "Check" =>{
      sender() ! StateResponse(is_started,context.system.uptime)
    }
  }
}
