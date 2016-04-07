package org.qq.crawler

import akka.actor.{ActorRef, Actor}
import org.qq.login.{roundrobinQQs, QQ}
import spray.json._
/**
  * Created by Scott on 3/29/16.
  */
case object CheckQQState
case object SelfChecking
class QQvalidator(original_qq:QQ,requestRouter:ActorRef) extends Actor with Validate{
  var valid_qqs = new roundrobinQQs(Array(original_qq))
  def receive ={
    case AddQQRequest(qq) =>{
      if(validate_twice(qq)){
        val new_validated = new roundrobinQQs(valid_qqs.QQs.+:(qq))
        requestRouter ! ChangeValidQQs(new_validated)
        valid_qqs = new_validated
      }
    }
    case SelfChecking =>{
      val new_validate = new roundrobinQQs(valid_qqs.QQs.filter(validate_twice(_)))
    }
  }
}
trait Validate extends ShuoshuoRequester{
  def validate(qq:QQ):Boolean = {
    val test_target = "649899819"//well, that's mine
    try{
      getUserShuoshuo(qq,test_target).parseJson.asJsObject.getFields("subcode").head.toString.toInt >= 0
    }
    catch {
      case _:Throwable => false
    }
  }
  def validate_twice(qq:QQ):Boolean = validate(qq) || validate(qq)
}
