package org.qq.data


import org.elasticsearch.index.query.QueryBuilders
import org.scalatest.{FlatSpec, ShouldMatchers}
import scala.collection.JavaConversions._
import org.qq.ConfigObj
/**
  * Created by Scott on 4/1/16.
  */
class ESTest extends FlatSpec with ShouldMatchers{
  val es = ES()
  val indice = "qq"
  val typo = "shuoshuo"
  "A search result" should "have multiple hits" in{
    val field = "content"
    val term = "e7085"
    val data = "{\n               \"cmtnum\": \"2\",\n               \"uin\": \"850016536\",\n               \"source_name\": \"\",\n               \"content\": \"一晚上能看五本言情小说[em]e7085[/em]\",\n               \"time\": \"1459599519\",\n               \"fwdnum\": \"0\",\n               \"nickname\": \"肉多多\"\n            }"
    es.index(indice,typo,data,"1").actionGet()
    val query = QueryBuilders.queryStringQuery(term)
    val raw_result = es.getClient.prepareSearch(indice).setTypes(typo).setQuery(query).setFrom(0).setSize(60).execute().get()
    raw_result.getHits.hits().map(_.getSourceAsString).toList.length should be >= 0
    es.delele(indice,typo,"1").actionGet()
  }
}
