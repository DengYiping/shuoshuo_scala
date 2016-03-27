package org.qq.common

import org.qq.login.QQ
import spray.json.JsObject

/**
  * Created by Scott on 3/21/16.
  */
abstract class ES_Storable{
  def id:String
  def data:AnyRef
}

case class Shuoshuo_doc(id:String, data:String) extends ES_Storable
case class Json_doc(id:String, data:String) extends ES_Storable
case class Map_doc(id:String, data:Map[String,String]) extends ES_Storable
case class Raw_doc(indice:String,typo:String,id:String,data:String) extends ES_Storable

case class ShuoShuoJsResponse(qq:String, js:JsObject)
case class Target(qq:String)
case class QQrequest(logined_qq:QQ, target:String)
