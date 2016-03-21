package org.qq.parser

/**
  * Created by Scott on 3/21/16.
  */
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
class RegexTest extends FlatSpec with ShouldMatchers {
  val qq_reg = """"uin":(\d+)""".r
  val test = "{\"name\":\"\",\"pos_y\":\"\",\"id\":\"\",\"pos_x\":\"\",\"idname\":\"\"},\"uin\":649899819,\"source_name\":\"\",\"content"
  "Regex" should "be able to find qq numbers" in {
    val i = qq_reg.findAllMatchIn(test).map(mat =>  mat.group(1)).toList.size should be > (0)
  }
}
