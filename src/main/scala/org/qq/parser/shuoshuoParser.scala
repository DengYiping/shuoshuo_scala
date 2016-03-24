package org.qq.parser
/**
  * Created by Scott on 3/21/16.
  */
import spray.json._
object shuoshuoParser {
  def apply(ss:JsObject):String = {
    val statusCode = ss.getFields("subcode").head.toString.toInt
    if(statusCode >= 0){
      ss.getFields("msglist").head.asInstanceOf[JsArray].elements.head.compactPrint
    }
    else{
      throw new Throwable
    }
  }
  def main(Args:Array[String]): Unit ={
    val test = "{\"hello\":\"world\"}"
    val msgs = "[\n{ \"firstName\":\"Bill\" , \"lastName\":\"Gates\" },\n{ \"firstName\":\"George\" , \"lastName\":\"Bush\" },\n{ \"firstName\":\"Thomas\" , \"lastName\": \"Carter\" }\n]"
    val test2 = test.substring(0,test.length-1) + ",\"msglist\":" + msgs + "}"
    println(test2)
    val test3 = test2.parseJson.asJsObject.getFields("msglist").head.asInstanceOf[JsArray].elements.head
    println(test3)
  }
}
