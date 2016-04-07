package org.qq.data


import org.elasticsearch.index.query.QueryBuilders
import org.scalatest.{FlatSpec, ShouldMatchers}
import scala.collection.JavaConversions._
/**
  * Created by Scott on 4/1/16.
  */
class ESReadTest extends FlatSpec with ShouldMatchers{
  val es = ES()
  val indice = "qq"
  val typo = "shuoshuo"
  "A search result" should "have multiple hits" in{
    val field = "content"
    val term = "hello"
    val query = QueryBuilders.queryStringQuery(term)
    val raw_result = es.getClient.prepareSearch(indice).setTypes(typo).setQuery(query).setFrom(0).setSize(60).execute().get()
    raw_result.getHits.hits().map(_.getSourceAsString).toList.length should be >= 0
  }
}
