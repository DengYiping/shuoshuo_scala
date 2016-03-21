package org.qq.parser
import org.qq.parser.Bloomfilter
/**
  * Created by Scott on 3/21/16.
  */
import spray.json._
object shuoshuoParser {
  def apply(ss:JsObject):String = {
    val statusCode = ss.getFields("subcode").head.toString.toInt
    if(statusCode >= 0){
      val msgs = ss.getFields("msglist").head.compactPrint
      val info = ss.getFields("usrinfo").head.compactPrint
      "{" + "\"info\":"+ info + ",\"msgs\":" + msgs + "}"
    }
    else{
      val info = ss.getFields("usrinfo").head.compactPrint
      "{" + "\"info\":"+ info + "}"
    }
  }
}
