package org.qq.crawler

import akka.actor.{ActorLogging, ActorRef, Actor}
import org.qq.login.{roundrobinQQs, QQ}
import spray.json._
import scala.concurrent.duration._
import org.qq.common.ShuoshuoConfig
/**
  * Created by Scott on 3/29/16.
  */
case object SelfChecking
class QQvalidator(original_qq:QQ,requestRouter:ActorRef) extends Actor with Validate with ActorLogging{
  var valid_qqs = new roundrobinQQs(Array(original_qq))
  implicit val disp = context.system.dispatcher
  val cancellable =
    context.system.scheduler.schedule(30 seconds,
      ShuoshuoConfig.qq_check_freq minutes,
      self,
      SelfChecking)
  def update():Unit ={
    if(valid_qqs.QQs.length > 0){
      requestRouter ! ChangeValidQQs(valid_qqs)
      log.info("qq list updated")
    }
    else {
      context.parent ! "Stop"
    }
  }
  def receive ={
    case AddQQRequest(qq) =>{
      if(validate_twice(qq)){
        valid_qqs = new roundrobinQQs(valid_qqs.QQs.+:(qq))
        update()
      }
    }
    case SelfChecking =>{
      valid_qqs = new roundrobinQQs(valid_qqs.QQs.filter(validate_twice(_)))
      update()
    }
  }
  override def postStop(): Unit = {
    cancellable.cancel()
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
