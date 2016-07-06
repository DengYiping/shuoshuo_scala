package org.qq.common

/**
  * Created by Scott on 4/7/16.
  */
import com.typesafe.config.ConfigFactory

case object ShuoshuoConfig {
  private val conf = ConfigFactory.load()
  val worker_num = conf.getInt("crawler.worker_num")
  val qq_check_freq = conf.getInt("crawler.qq_check_freq")
  val cluster_name = conf.getString("es.cluster_name")
  val es_ip = conf.getString("es.ip")
}
