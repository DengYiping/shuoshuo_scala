package org.qq.parser
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
  val fetcher = context.actorOf(Props[QQFetcher],"Fetcher")
  def receive = {
    case w:SsResponse => parser ! w
    case ParsedResponse(qq_num,parsed) =>{
      fetcher ! parsed
      es ! Shuoshuo_data(qq_num,parsed)
    }
    case Target(qq_target) =>{
      req ! QQrequester(qq,qq_target)
      log.info("New task:" + qq_target)
    }
  }
}
