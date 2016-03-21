package org.qq.requests

/**
  * Created by Scott on 1/13/16.
  */
import spray.json._
import DefaultJsonProtocol._
import org.qq.login.QQ
object QQrequest {
  private val baseShuoShuoURL1 = "http://taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6?uin="
  private val baseShuoShuoURL2 = "&num=5&replynum=5&g_tk="
  //crawl 5 shuoshuos per user, 5 comment per shuoshuo
  private val baseShuoShuoURL3 = "&format=jsonp"
  val jsonExtractor = """_Callback\((.*)\);""".r

  def getUserShuoshuo(working_qq:QQ, target_qq:String):JsObject ={
    val url = baseShuoShuoURL1 + target_qq + baseShuoShuoURL2 + working_qq.gtk + baseShuoShuoURL3
    val jsonExtractor(response) = HTTP.get(url,Some(working_qq.cookies))
    response.parseJson.asJsObject
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
    //TODO it is currently broken
    val url = baseInfoURL + target_qq + "&g_tk=" + working_qq.gtk
    val jsonExtractor(response) = HTTP.get(url,Some(working_qq.cookies)).replace("\n"," ")
    response.parseJson.asJsObject
  }

  def main(args:Array[String]): Unit ={
    val newQQ = QQ(649899819,"@DuxryYEDo")
    val ss = getUserShuoshuo(newQQ,"7896178")
    println(ss getFields "code" head)
    println(ss.prettyPrint)
  }
}
