package org.qq.common

import org.qq.login.QQ
import spray.json.JsObject

/**
  * Created by Scott on 3/21/16.
  */

case class ShuoShuoJsResponse(qq:String, js:JsObject)
case class Target(qq:String)
case class QQrequest(logined_qq:QQ, target:String)
abstract class Result extends {def msg:String}
case object Succeed extends Result{
  val msg = "Succeeded"
}
case object Failed extends Result{
  val msg = "Failed"
}
case class StateResponse(isStarted:Boolean, uptime:Long)
case class ESCount(count:Long)