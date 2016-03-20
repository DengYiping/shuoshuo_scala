package org.qq.login

/**
  * Created by Scott on 1/18/16.
  */
class roundrobinQQs(QQs:Array[QQ]) {
  private val max = QQs.length - 1
  private var i = 0

  def get: QQ = {
    val value = QQs(i)
    if(i == max)
      i = 0
    else
      i += 1
    value
  }
}