package org.qq.crawler
import org.qq.ConfigObj
/**
  * Created by Scott on 3/30/16.
  */

import org.qq.login.QQ
import org.scalatest.FlatSpec
import org.scalatest.ShouldMatchers
class ValidateTest extends FlatSpec with ShouldMatchers with Validate{
  "A qq number validator" should "validate a properly configured qq and its skey" in{
    validate(ConfigObj.qq) should be (true)
  }
}
