package org.qq.parser

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import spray.json._
import org.qq.login.QQ
import org.qq.data.{ESactor,ES}
import org.qq.requests.{HTTPexception, RequestHandler, RequestRouter}
/**
  * Created by Scott on 3/21/16.
  */
import org.qq.common._
class ResponseHandler(qq:QQ) extends Actor with ActorLogging {
  val es = context.actorOf(Props(new ESactor(ES.apply())),"Elasticsearch")
  val req = context.actorOf(Props[RequestRouter],"Requester")
  val parser = context.actorOf(Props[Parser],"Parser")
  override val supervisorStrategy= OneForOneStrategy() {
    case _:Throwable => Restart
  }
  def receive = {
    case w:ShuoShuoJsResponse => parser ! w

    case Target(qq_target) =>{
      req ! QQrequest(qq,qq_target)
    }
    case x:ES_Storable => es ! x
  }
}
