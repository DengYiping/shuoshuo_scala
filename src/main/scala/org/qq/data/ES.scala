package org.qq.data

import org.elasticsearch.common.settings.Settings
import org.qq.login.QQ
import org.qq.requests.QQrequest
import org.qq.parser.shuoshuoParser
/**
  * Created by Scott on 1/13/16.
  */


import java.net.InetAddress

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import scala.collection.JavaConversions._


object ES{
  def getLocalclient: Client = {
    val settings = Settings.settingsBuilder()
      .put("cluster.name", "elasticsearch")
      .put("client.transport.sniff", true)
      .build()

    //return
    TransportClient.builder().settings(settings).build()
      .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9300))
  }
  def apply():ES = new ES(this.getLocalclient)
  def cleanIndex(client:Client):Unit = {
    val response = client.admin.cluster.prepareState.execute.actionGet()
    val indices = response.getState.getMetaData.getConcreteAllIndices.toList
    indices.foreach( indice => client.admin().indices.prepareDelete(indice).execute().actionGet() )
  }
  def main(Args:Array[String]): Unit = {
    val qq = QQ(649899819L, "@SrtunOYpS")
    val es = ES()
    val target_qq = "649899819"
    val qq_json = QQrequest.getUserShuoshuo(qq,target_qq)
    val parsed_shuoshuo = shuoshuoParser(qq_json)
    val response = es.index("qq","shuoshuo",parsed_shuoshuo,target_qq)
    println("Response:" + response.getVersion.toString)
    //es.cleanAll()
    es.close()
  }
}

class ES(private val client:Client){
  final def close() = client.close()
  def append(indice:String, typo:String, json:String) = client
    .prepareIndex(indice,typo)
    .setSource(json)
    .execute
    .actionGet
  def index(indice:String,typo:String, json:String, id:String) = client
    .prepareIndex(indice,typo)
    .setId(id)
    .setSource(json)
    .execute()
    .actionGet
  def delele(indice:String, typo:String, id:String) = client.prepareDelete(indice,typo,id).execute().actionGet
  def get(indice:String, typo:String, id:String) = client.prepareGet(indice, typo,id).execute().actionGet
  def cleanAll():Unit = ES.cleanIndex(client)
  def getClient = client
}
