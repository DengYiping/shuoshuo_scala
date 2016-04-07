package org.qq.web

import akka.actor._
import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import spray.json.DefaultJsonProtocol._
import spray.routing.HttpService
import spray.json._
import spray.httpx.SprayJsonSupport._
import scala.concurrent.duration._
import akka.pattern.ask

/**
  * Created by Scott on 3/30/16.
  */
class MainServiceActor(val crawler:ActorRef, val es_actor:ActorRef) extends MainService{
  val superviser = context.parent
  def actorRefFactory = context
  def receive = runRoute(route)
}
