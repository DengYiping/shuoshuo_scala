package org.qq.data
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.actor._
import akka.event.LoggingReceive
import org.qq.common._
import scala.collection.JavaConversions._
/**
  * Created by Scott on 3/21/16.
  */
class ESactor(es:ES) extends Actor with ActorLogging{
  private var count = 0
  val freq = 1000
  def receive = {
    case Shuoshuo_doc(qq,json) => {
      es.index("qq","shuoshuo",json,qq)

      count = count + 1
      if(count % freq == 0){
        log.info("Elasticsearch total count:" + count.toString)
      }
    }
    case Json_doc(id,json) =>{
      es.index("qq","shuoshuo",json,id)

      count = count + 1
      if(count % freq == 0){
        log.info("Elasticsearch total count:" + count.toString)
      }
    }
    case Map_doc(id,m) =>{
      val temp = es.getClient.prepareIndex("qq","shuoshuo").setId(id)
      temp.execute().actionGet()
      count = count + 1
      if(count % freq == 0){
        log.info("Elasticsearch total count:" + count.toString)
      }
    }
    case Raw_doc(indice,typo,id,data) =>{
      es.index(indice,typo,data,id)
      count = count + 1
      if(count % freq == 0){
        log.info("Elasticsearch total Count:" + count.toString)
      }
    }
  }
}
