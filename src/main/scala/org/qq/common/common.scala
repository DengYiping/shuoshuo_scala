package org.qq.common

import org.qq.login.QQ
import spray.json.JsObject

/**
  * Created by Scott on 3/21/16.
  */
case class Shuoshuo_data(qq:String,json:String)
case class SsResponse(qq:String, js:JsObject)
case class Target(qq:String)
case class QQrequester(logined_qq:QQ,target:String)
case class ParsedResponse(qq:String, js:String)
