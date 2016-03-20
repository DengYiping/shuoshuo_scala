package org.qq.requests

import java.net.HttpCookie

/**
  * Created by Scott on 1/12/16.
  */

case class HTTPexception(e:String) extends Throwable
object HTTP {
  import scalaj.http._
  private val reg_200 = """.*(200).*""".r
  def get(url:String, cookies:Option[Map[String,String]]): String = {

    val javaCookies = cookies match {
      case Some(x) => x.map(y => new HttpCookie(y._1,y._2)).toSeq
      case None => Seq()
    }

    val request = Http(url)
    .header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
    .cookies(javaCookies)
    val response = try{
      request.asString
    }catch{
      case _:Throwable => throw new HTTPexception("Error in perform HTTP request due to network issue")
    }

    if(response.isSuccess)
      reg_200 findFirstIn response.statusLine match{
        case Some(x) => response.body
        case None => throw new HTTPexception("Wrong status code")
      }
    else
      throw new HTTPexception("Error in get data, maybe it is a bad URL")
  }

  def main(args:Array[String]): Unit ={
    println(get("https://www.google.com",None))
  }

}
