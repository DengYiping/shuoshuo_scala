package org.qq.common.ES

import spray.json.JsObject

/**
  * Created by Scott on 3/31/16.
  */
/**
  * data type that is able to store in elasticsearch
  */

abstract class ES_Action
sealed trait ES_Storable extends ES_Action{
  def id:String
  def data:AnyRef
}
case class Shuoshuo_doc(id:String, data:String) extends ES_Storable
case class Json_doc(id:String, data:String) extends ES_Storable
case class Map_doc(id:String, data:Map[String,String]) extends ES_Storable
case class Raw_doc(indice:String,typo:String,id:String,data:String) extends ES_Storable

trait Read_ES extends ES_Action
case class ESSearchRequest(indice:String, typo:String, field:Option[String], term:String) extends Read_ES

case class SearchResult(json:String)
