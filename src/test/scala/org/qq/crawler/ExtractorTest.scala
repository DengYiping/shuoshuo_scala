package org.qq.crawler
import org.qq.ConfigObj
/**
  * Created by Scott on 3/30/16.
  */

import org.qq.common.Bloomfilter
import org.scalatest.FlatSpec
import org.scalatest.ShouldMatchers
import spray.json._
class Extractor_ShuoshuoRequester_ParserTest extends FlatSpec with ShouldMatchers with QQextractor with ShuoshuoRequester with Parser{
  val filter = Bloomfilter[String](500000000,5) //if not in it return True, otherwise False
  val shuoshuo = getUserShuoshuo(ConfigObj.qq,"649899819")
  "A Shuoshuo Requester" should "download the json data easily" in{
    shuoshuo.length should be > 0
  }
  it should "be able to parse into json without any error" in{
    shuoshuo.parseJson.asJsObject.prettyPrint.length should be > 0
  }
  "A Extractor" should "extract qq numbers for further research" in{
    fetchQQ(shuoshuo).length should be > 0
  }
  "A parser" should "parse the raw data and put it in a proper form" in{
    shuoshuoJsParse(shuoshuo.parseJson.asJsObject).length should be > 0
  }
}
