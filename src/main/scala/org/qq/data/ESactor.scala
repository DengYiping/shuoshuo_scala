package org.qq.data

import org.elasticsearch.index.query.{QueryBuilders,QueryBuilder}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.actor._
import akka.event.LoggingReceive
import org.qq.common._
import scala.collection.JavaConversions._
import org.qq.main.toporder.ESclean
import org.qq.common.ES._
import spray.json._
import spray.json.DefaultJsonProtocol._
/**
  * Created by Scott on 3/21/16.
  */
class ESactor(val es:ES) extends Actor with ActorLogging{
  private var count = 0L
  val log_freq = 1000L
  def receive = {
    case data_store:ES_Storable =>{
      data_store match{
        case Shuoshuo_doc(qq,json) => {
          es.index("qq","shuoshuo",json,qq)
          count = count + 1L
          if(count % log_freq == 0){
            log.info("Elasticsearch total count:" + count.toString)
            context.parent ! ESCount(count)
          }
        }
        case Json_doc(id,json) =>{
          es.index("qq","shuoshuo",json,id)
          count = count + 1L
          if(count % log_freq == 0){
            log.info("Elasticsearch total count:" + count.toString)
            context.parent ! ESCount(count)
          }
        }
        case Map_doc(id,m) =>{
          val temp = es.getClient.prepareIndex("qq","shuoshuo").setId(id)
          temp.execute().actionGet()
          count = count + 1L
          if(count % log_freq == 0){
            log.info("Elasticsearch total count:" + count.toString)
            context.parent ! ESCount(count)
          }
        }
        case Raw_doc(indice,typo,id,data) =>{
          es.index(indice,typo,data,id)
          count = count + 1L
          if(count % log_freq == 0){
            log.info("Elasticsearch total Count:" + count.toString)
            context.parent ! ESCount(count)
          }
        }
      }
    }
    case ESclean => es.cleanAll()
    /*case data_read:Read_ES =>{
      data_read match{
        case ESSearchRequest(indice, typo, field, term) =>{
          val result = es.getClient.prepareSearch(indice)
            .setTypes(typo)
            .setQuery(field match {
              case Some(name:String) => QueryBuilders.termQuery(name,term)
              case None => QueryBuilders.queryStringQuery(term)
            })
            .setFrom(0)
            .setSize(60)
            .execute()
            .get().getHits.hits().map(_.getSourceAsString)
          sender() ! SearchResult(result mkString ",\n")
        }
      }
    } */
  }
}