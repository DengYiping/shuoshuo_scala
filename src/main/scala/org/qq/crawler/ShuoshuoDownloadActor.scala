package org.qq.crawler

import java.net.HttpCookie

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.routing.{SmallestMailboxRoutingLogic, Router, ActorRefRoutee}
import org.qq.login.{roundrobinQQs, QQ}
import spray.json._
import scalaj.http._
import scala.util.matching.Regex
import org.qq.common._

/**
  * Created by Scott on 3/29/16.
  */
case class AddQQRequest(new_qq:QQ)
case class ChangeValidQQs(new_qqs:roundrobinQQs)

class RequestRouter(parser:ActorRef,original_qq:QQ) extends Actor{
  val worker_num = 15 //modify this number if you have a better server
  val qq_extractor = context.actorOf(Props(new QQextractorActor(self)))//extract qqs
  override val supervisorStrategy= OneForOneStrategy() {
    case _:Throwable => Restart
  }
  var worker_qqs:roundrobinQQs = new roundrobinQQs(Array(original_qq))
  var router = {
    val routees = Vector.fill(worker_num) {
      val r = context.actorOf(Props(new ShuoshuoDownloaderWorker(parser,qq_extractor)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(SmallestMailboxRoutingLogic(), routees)
  }

  def receive = {
    case Target(target) =>{
      router.route(QQrequest(worker_qqs.get,target), self)
    }
    case ChangeValidQQs(new_qqs) => worker_qqs = new_qqs
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props(new ShuoshuoDownloaderWorker(parser,qq_extractor)))
      context watch r
      router = router.addRoutee(r)
  }
}

class ShuoshuoDownloaderWorker(parser:ActorRef, qq_extractor:ActorRef) extends Actor with ShuoshuoRequester{
  def receive = {
    /**
      * In order to get around the Excecption
      */
    case QQrequest(worker_qq, target) =>{
      val response = try{
        getUserShuoshuo(worker_qq,target)
      }catch {
        case e:Throwable => ""
      }
      if (response.length > 0){
        qq_extractor ! response
        try{
          parser ! ShuoShuoJsResponse(target,response.parseJson.asJsObject)
        }catch{case _:Throwable =>}
      }
    }
  }
}

case class HTTPexception(e:String) extends Throwable

trait GetRequest{
  private val reg_200 = """.*(200).*""".r
  private val user_agent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"

  def getRequest(url:String, cookies:Option[Map[String,String]]): String = {
    val javaCookies = cookies match {
      case Some(x) => x.map(y => new HttpCookie(y._1,y._2)).toSeq
      case None => Seq()
    }
    val request = Http(url)
      .header("User-Agent",user_agent)
      .cookies(javaCookies)
    val response = try{
      request.asString
    }catch{
      case _:Throwable => throw new HTTPexception("Error in perform HTTP request due to network issue")
    }
    if(response.isSuccess)
      reg_200 findFirstIn response.statusLine match{
        case Some(x) => response.body
        case None => throw new HTTPexception("Status code error")
      }
    else
      throw new HTTPexception("Error in get data, maybe it is a bad URL")
  }
}

trait ShuoshuoRequester extends GetRequest{
  private val baseShuoShuoURL1 = "http://taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6?uin="
  private val baseShuoShuoURL2 = "&num=5&replynum=5&g_tk="
  //crawl 5 shuoshuos per user, 5 comment per shuoshuo
  private val baseShuoShuoURL3 = "&format=jsonp"
  val jsonExtractor = """_Callback\((.*)\);""".r //remove the callback function call and extracting unparsed json

  def getUserShuoshuo(working_qq:QQ, target_qq:String):String ={
    val url = baseShuoShuoURL1 + target_qq + baseShuoShuoURL2 + working_qq.gtk + baseShuoShuoURL3
    val jsonExtractor(response) = getRequest(url,Some(working_qq.cookies))
    response //parse json
  }

  private val baseInfoURL = "http://user.qzone.qq.com/p/base.s8/cgi-bin/user/cgi_userinfo_get_all?uin="
  /**
    * Get user information
    *
    * Example url: http://user.qzone.qq.com/p/base.s8/cgi-bin/user/cgi_userinfo_get_all?uin=635918476&vuin=649899819&fupdate=1&rd=0.6897570351138711&g_tk=887436293
    * @param working_qq the logined qq
    * @param target_qq qq we need to crawl
    */
  @Deprecated def getUserProfile(working_qq:QQ, target_qq:String):JsObject = {
    val url = baseInfoURL + target_qq + "&g_tk=" + working_qq.gtk
    val jsonExtractor(response) = getRequest(url,Some(working_qq.cookies)).replace("\n"," ")
    response.parseJson.asJsObject
  }
}
