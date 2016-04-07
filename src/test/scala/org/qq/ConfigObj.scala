package org.qq

import com.typesafe.config.ConfigFactory
import org.qq.login.QQ

/**
  * Created by Scott on 4/7/16.
  */
object ConfigObj {
  private val conf = ConfigFactory.load("test.conf")
  val qq_num = conf.getLong("qq.num")
  val skey = conf.getString("qq.skey")
  val qq = QQ(qq = qq_num,skey)
}

