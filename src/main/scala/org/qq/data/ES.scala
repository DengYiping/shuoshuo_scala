package org.qq.data

import org.elasticsearch.common.settings.Settings
import org.qq.login.QQ
import org.qq.requests.QQrequest
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
      .put("cluster.name", "elasticsearch_Scott")
      .put("client.transport.sniff", true)
      .build()

    //return
    TransportClient.builder().settings(settings).build()
      .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9300))
  }
  def cleanIndex(client:Client):Unit = {
    val response = client.admin.cluster.prepareState.execute.actionGet()
    val indices = response.getState.getMetaData.getConcreteAllIndices.toList
    indices.foreach( indice => client.admin().indices.prepareDelete(indice).execute().actionGet() )
  }
  def main(Args:Array[String]): Unit = {
    val qq = QQ(649899819L, "@8xf4JFEpc")
    val client = this.getLocalclient
    val json = "{qq:649899819, ide:1}"
    val response = client.prepareIndex("testindex","test").setId("1").setSource(json).execute().actionGet()
    println("Response:" + response.getVersion.toString)
    this.cleanIndex(client)
    client.close()
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
  def getClient = client
}
