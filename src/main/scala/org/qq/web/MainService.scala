package org.qq.web

import akka.util.Timeout
import org.qq.crawler.AddQQRequest
import org.qq.login.QQ
import org.qq.main.toporder.CrawlerStart
import spray.json.DefaultJsonProtocol._
import spray.routing.HttpService
import spray.json._
import spray.httpx.SprayJsonSupport._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor._
import spray.http.StatusCodes.NotFound
import org.qq.common.ES._
import akka.pattern.ask
import org.qq.common._
import scala.concurrent.Future
/**
  * Created by Scott on 3/30/16.
  */
object MasterJson extends DefaultJsonProtocol{
  implicit val hello = jsonFormat4(StateReport)
}
case class StateReport(isUp:Boolean,uptime:Long,storage_count:Long, crawling_rate:Double)
trait MainService extends HttpService with Actor{
  implicit val timeout = Timeout(5 seconds)
  val es_actor:ActorRef
  val crawler:ActorRef
  val superviser:ActorRef
  val route = {
    val dir = "html/"
    path("search" / Rest){  path =>
      parameters('q,'field.?) { (q,field) =>
        detach(){
          jsonpWithParameter("jsonp"){
            complete{
              val future = es_actor ? ESSearchRequest("qq","shuoshuo",field,q)
              val result = try{Await.result(future, timeout.duration).asInstanceOf[SearchResult]} catch {
                case _:Throwable =>SearchResult("")
              }
              ("[" + result.json + "]").parseJson.asInstanceOf[JsArray]
            }
          }
        }
      }
    }~
      pathEndOrSingleSlash {
        getFromResource(dir + "index.html")
      }~
    path("admin" / "start"/ Rest){
      path => parameters('qq.as[Long],'skey,'seed.?){
        (qq:Long,skey:String,seed:Option[String]) =>{
          crawler ? CrawlerStart(QQ(qq,skey)
            ,List(seed match{
            case Some(x:String) =>x
            case None => qq.toString
          }))
          jsonpWithParameter("jsonp"){
            complete("{\"result\":\"complete\"}".parseJson.asJsObject())
          }
        }
      }
    }~
    path("admin" / "check" / Rest){
      path => jsonpWithParameter("jsonp"){
        val check_future = (crawler ? "Check").asInstanceOf[Future[StateResponse]]
        detach(){
          val check_future = (crawler ? "Check").asInstanceOf[Future[StateResponse]]
          import MasterJson._
          val result1:StateResponse = try{Await.result(check_future, timeout.duration)} catch {
            case _:Throwable => StateResponse(false,0L)
          }
          val result2 = try{Await.result((es_actor ? "Count"), timeout.duration).asInstanceOf[Long]} catch {
            case _:Throwable =>0L
          }
          val result = if(result1.uptime <= 0 || result1.isStarted == false) StateReport(false,0,0,0)
          else StateReport(result1.isStarted,result1.uptime,result2,(result2.toDouble / result1.uptime.toDouble))
          complete(result)
        }
      }
    }~
    path("admin" / "addtarget" / Rest){
      path => parameters('qq) {
        (qq) => jsonpWithParameter("jsonp"){
          crawler ! Target(qq)
          complete("{\"state\":\"success\"}".parseJson.asJsObject)
        }
      }
    }~
    path("admin" / "addworker" / Rest){
      path => parameters('qq,'skey){
        (qq,skey) => jsonpWithParameter("jsonp"){
          crawler ! AddQQRequest(QQ(qq.toLong,skey));
          complete("{\"state\":\"success\"}".parseJson.asJsObject)
        }
      }
    }~
    path("util" / "getg_tk" / Rest){
      path => parameters('qq,'skey){
        (qq,skey) => jsonpWithParameter("jsonp"){
          val new_qq = QQ(qq.toLong, skey)
          val g_tk = new_qq.gtk
          complete(("{\"g_tk\":\"" + g_tk + "\"}").parseJson.asJsObject)
        }
      }
    }~
    pathPrefix("") {
        getFromResourceDirectory(dir)
      } ~ complete(NotFound)
  }
}
