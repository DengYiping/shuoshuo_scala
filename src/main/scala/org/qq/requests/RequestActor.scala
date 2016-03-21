package org.qq.requests

/**
  * Created by Scott on 3/21/16.
  */

import akka.actor.SupervisorStrategy.Restart

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.actor._
import akka.event.LoggingReceive
import org.qq.login.QQ
import org.qq.data.ESactor
import akka.routing.{ ActorRefRoutee, SmallestMailboxRoutingLogic, Router }
import org.qq.common._

class RequestHandler extends Actor with ActorLogging{
  def receive = {
    case QQrequester(logined_qq,target) =>{
      try{
        val rep = SsResponse(target,QQrequest.getUserShuoshuo(logined_qq,target))
        sender() ! rep
      }
      catch{
        case _:Throwable =>
      }
    }
  }
}

class RequestRouter extends Actor{
  override val supervisorStrategy= OneForOneStrategy() {
    case HTTPexception(e) => Restart
  }
  var router = {
    val routees = Vector.fill(7) {
      val r = context.actorOf(Props[RequestHandler])
      context watch r
      ActorRefRoutee(r)
    }
    Router(SmallestMailboxRoutingLogic(), routees)
  }
  def receive = {
    case w: QQrequester =>
      router.route(w, sender())
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[RequestHandler])
      context watch r
      router = router.addRoutee(r)
  }
}
