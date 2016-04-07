package org.qq.crawler

import spray.json.{JsArray, JsValue, JsObject}
import akka.actor.{ActorRef, Actor}
import spray.json._
import DefaultJsonProtocol._
import org.qq.common._
import ES._
import scala.collection.mutable.Map
import scala.collection.{mutable, immutable}
/**
  * Created by Scott on 3/28/16.
  */
class ParserActor(val esactorRef:ActorRef) extends Actor with Parser{
  def receive = {
    case ShuoShuoJsResponse(qq,js) =>{
      shuoshuoJsParse(js).foreach(m =>esactorRef ! Json_doc(id = m._1,data = m._2.toJson.compactPrint))
    }
  }
}
//parse raw json object to a List of keys and maps of fields of each shuoshuo
trait Parser{
  def shuoshuoJsParse(ss:JsObject):(List[(String,immutable.Map[String,String])])= {
    val statusCode = ss.getFields("subcode").head.toString.toInt

    def dequotation(original:String):String = {
      if(original.length < 2){
        original
      }
      else if(original.charAt(0) != '\"' || original.last != '\"'){
        original
      }
      else dequotation(original.substring(1,original.length -1))
    }
    //tid is a unique id of every shuoshuo
    def get_tid(jsv:JsValue):String = {
      dequotation(jsv.asJsObject().getFields("tid").head.toString())
    }

    def make_map(jsv:JsValue):immutable.Map[String,String] = {
      val obj = jsv.asJsObject
      val auxMap = Map[String,String]()
      def BuilderAdd(key:String):Unit = {
        if(obj.getFields(key).length > 0){
          auxMap += (key -> dequotation(obj.getFields(key).head.toString()))
        }
      }
      val main_info = immutable.Map(
        "uin" -> dequotation(obj.getFields("uin").head.toString()),
        "nickname" -> dequotation(obj.getFields("name").head.toString()),
        "time" -> dequotation(obj.getFields("created_time").head.toString())
      )
      BuilderAdd("content")
      BuilderAdd("source_name")
      BuilderAdd("fwdnum")
      BuilderAdd("cmtnum")
      main_info ++ auxMap.toMap
    }

    try{
      if(statusCode >= 0){
        ss.getFields("msglist").head.asInstanceOf[JsArray].elements.map(jsv => (get_tid(jsv),make_map(jsv)))
      }.toList
      else{
        Nil
      }
    }catch {
      case _:Throwable => Nil
    }
  }
}

