package org.qq.login

/**
  * Created by Scott on 1/13/16.
  * This file contains login info
  */
class QQ(val qq:Long, private val skey:String, val gtk:String)
{
  private def fillDigit(raw:String):String = {
    if(raw.length == 10)
      raw
    else
      fillDigit("0" + raw)
  }

  private val uin = "o" + fillDigit(qq.toString)
  /**
    * reture a necessary cookies for crawling shuoshuo
    */
  val cookies = Map(
    "uin" -> uin,
    "skey" -> skey
  )

}
object QQ{
  /**
    * this is a simple forward constructor
    * @param qq qq number
    * @param skey skey
    * @param gtk gtk will be used in url
    * @return QQ
    */
  def apply(qq:Long,skey:String, gtk:String):QQ = new QQ(qq,skey,gtk)

  /**
    * this constructor automatically calculate gtk
    * @param qq: QQ number
    * @param skey: the skey
    * @return QQ
    */
  def apply(qq:Long,skey:String):QQ = {
    def getGT_K(sk:String):String = {
      var hash = 5381
      val max = sk.length
      var i = 0
      while(i < max){
        hash += (hash << 5) + sk.charAt(i)
        i += 1
      }
      hash = hash & 0x7fffffff;
      hash.toString
    }

    QQ(qq,skey,getGT_K(skey))
  }
}

