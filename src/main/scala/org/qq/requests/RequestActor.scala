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
  case object Fuck
  def receive = {
    case QQrequester(logined_qq,target) =>{
      val rep = try{SsResponse(target,QQrequest.getUserShuoshuo(logined_qq,target))}catch { case _:Throwable => Fuck}
      rep match {
        case x:SsResponse => sender() ! x
        case Fuck =>
      }
    }
  }
}

class RequestRouter extends Actor{
  override val supervisorStrategy= OneForOneStrategy() {
    case _:Throwable => Restart
  }
  var router = {
    val routees = Vector.fill(10) {
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
