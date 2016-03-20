package org.qq.requests

/**
  * Created by Scott on 1/12/16.
  */
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
class HTTPtest extends FlatSpec with ShouldMatchers{
  "A HTTP requester" should "be able to perform regular HTTP get request" in{
    val baidu = HTTP.get("http://www.baidu.com",None)
    baidu.length should be > (0)
  }
  it should "be able to perform HTTPS get request" in {
    val zhihu = HTTP.get("https://www.zhihu.com",None)
    zhihu.length should be > (0)
  }
  it should "be able to insert cookies like inserting Optional Map" in{
    val simpleMap = Map("Hello" -> "World")
    val baidu = HTTP.get("http://www.baidu.com",Some(simpleMap))
    baidu.length should be > (0)
  }
}
