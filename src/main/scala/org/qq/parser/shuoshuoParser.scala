package org.qq.parser
/**
  * Created by Scott on 3/21/16.
  */
import spray.json._
import DefaultJsonProtocol._
object shuoshuoParser {
  def apply(ss:JsObject):(List[(String,Map[String,String])])= {
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

    def make_map(jsv:JsValue):Map[String,String] = {
      val obj = jsv.asJsObject
      Map("uin" -> dequotation(obj.getFields("uin").head.toString()),
        "nickname" -> dequotation(obj.getFields("name").head.toString()),
        "content" -> dequotation(obj.getFields("content").head.toString()),
        "time" -> dequotation(obj.getFields("created_time").head.toString())
      )
    }
    if(statusCode >= 0){
      ss.getFields("msglist").head.asInstanceOf[JsArray].elements.map(jsv => (get_tid(jsv),make_map(jsv)))
    }.toList
    else{
      throw new Throwable
    }
  }

  def main(Args:Array[String]): Unit ={
    val test = "{\"secret\":0,\"source_url\":\"\",\"name\":\"Frankenstein\",\"t1_subtype\":1,\"created_time\":1452784380,\"source_appid\":\"\",\"conlist\":[{\"con\":\"中国对开源软件开发的支持相对落后，甚至很多人没有听过 \\\"开源\\\" \\\"open source\\\" \\\"FOSS\\\"。 在国外参加了不少开源活动，感触不少。 有在长沙地区做一个开源社区的想法。，在这之前先做一个开源活动的尝试，也顺带寻找有相同想法的同学。正值 Google Code-in 打得火热， 有点时间可以帮助 3 位 同学参与开源软件开发。 开源不一定要有超强的编程能力， 不一定要能理解复杂的数据结构。为开源软件的开发可以是一张海报，可以是一篇博文，可以是寻找游戏中的一个bug，也可以为操作系统添加一个新功能 To create a free world, we need you. Requirement: 18岁以下，高中生，有意愿参加Google Code－in，FOSSASIA， 有一定英语能力，出国党更佳（GCI能为你文书加不少分，不轻易放弃。 欢迎学弟学妹学长学姐qq我。\",\"type\":2}],\"createTime\":\"2016年01月14日\",\"wbid\":0,\"ugc_right\":1,\"cmtnum\":0,\"rt_sum\":0,\"issigin\":0,\"t1_source\":1,\"certified\":0,\"t1_termtype\":0,\"has_more_con\":0,\"lbs\":{\"name\":\"\",\"pos_y\":\"\",\"id\":\"\",\"pos_x\":\"\",\"idname\":\"\"},\"uin\":649899819,\"source_name\":\"\",\"content\":\"中国对开源软件开发的支持相对落后，甚至很多人没有听过 \\\"开源\\\" \\\"open source\\\" \\\"FOSS\\\"。 在国外参加了不少开源活动，感触不少。 有在长沙地区做一个开源社区的想法。，在这之前先做一个开源活动的尝试，也顺带寻找有相同想法的同学。正值 Google Code-in 打得火热， 有点时间可以帮助 3 位 同学参与开源软件开发。 开源不一定要有超强的编程能力， 不一定要能理解复杂的数据结构。为开源软件的开发可以是一张海报，可以是一篇博文，可以是寻找游戏中的一个bug，也可以为操作系统添加一个新功能 To create a free world, we need you. Requirement: 18岁以下，高中生，有意愿参加Google Code－in，FOSSASIA， 有一定英语能力，出国党更佳（GCI能为你文书加不少分，不轻易放弃。 欢迎学弟学妹学长学姐qq我。\",\"tid\":\"2bafbc26fcba975644c40400\",\"fwdnum\":0,\"pic_template\":\"\",\"right\":1}"
    val test3 = test.parseJson

    def dequotation(original:String):String = {
      if(original.length < 2){
        original
      }
      else if(original.charAt(0) != '\"' || original.last != '\"'){
        original
      }
      else dequotation(original.substring(1,original.length -1))
    }
    def make_map(jsv:JsValue):Map[String,String] = {


      val obj = jsv.asJsObject
      Map("uin" -> dequotation(obj.getFields("uin").head.toString()),
        "nickname" -> dequotation(obj.getFields("name").head.toString()),
        "content" -> dequotation(obj.getFields("content").head.toString()),
        "time" -> dequotation(obj.getFields("created_time").head.toString())
      )
    }
    def get_tid(jsv:JsValue):String = {
      dequotation(jsv.asJsObject().getFields("tid").head.toString())
    }
    println(make_map(test3).toJson)
  }
}
