package org.qq.login

/**
  * Created by Scott on 1/13/16.
  */
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
class RoundrobinTest extends FlatSpec with ShouldMatchers{
  "Round robin QQ data structure" should "be circular" in{
    val testArray = Array(QQ(1,"one","one"),QQ(2,"two","two"),QQ(3,"three","three"))
    val RRqq = new roundrobinQQs(testArray)
    for(i <- 1 to 50){
      RRqq.get.toString.length should be > (5)
      RRqq.get should not equal RRqq.get
      //stateful object change every time I call get
    }
  }
}

class QQtest extends FlatSpec with ShouldMatchers{
  "GT_K" should "equal to GT_K calculate from java" in{
    val skey = "@6Md3K2a3C"
    val testqq = QQ(649899819,skey)
    testqq.gtk should equal (Util.getGTK(testqq.cookies("skey")))
  }
}
