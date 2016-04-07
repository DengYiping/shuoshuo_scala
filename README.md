# shuoshuo_scala
[![Build Status](https://travis-ci.org/DengYiping/shuoshuo_scala.svg?branch=master)](https://travis-ci.org/DengYiping/shuoshuo_scala)


Shuoshuo_scala is a social network crawler for Tencent's Qzone(or Shuoshuo 说说). It is fully restful and asynchronized.


### Design Idea:

Shuoshuo_scala is a revised version of [Shuoshuo_crawler](https://github.com/DengYiping/Shuoshuo_crawler), but there are some difference:


Shuoshuo_scala | shuoshuo_crawler
-------------- | -----------------
uses Elastic Search as its data store | uses MongoDB
written in Scala and it's lock-free | written in C++
has a **RESTful** interface | a interface provided by MongoDB
provides full-text search | none

### Elastic Search Configuration(Optional):

Before you start the server, it is recommanded to configure Elasticsearch. Shuoshuo_scala still runs properly without configuration.

1. Install Elastic Search(the version below is a version with IK plugin, IK adds Chinese support to Elastic Search)
  <pre>git clone git://github.com/medcl/elasticsearch-rtf.git -b master --depth 1</pre>
  linux:
  <pre>cd elasticsearch/bin
  ./elasticsearch</pre>

  windows:
  <pre>cd elasticsearch/bin
  elasticsearch.bat</pre>
2. Add mappings to Elastic Search:
  ```
  curl -XPUT "http://localhost:9200/qq" -d'
{
    
  "mappings": {
    "shuoshuo": {
      "properties": {
        "time": {
          "type":   "date",
          "format": "epoch_second",
          "index": "not_analyzed"
        },
        "content":{
                "type" : "string",
                "index": "analyzed",
                "boost" : 20.0,
                "term_vector" : "with_positions_offsets",
                "analyzer": "ik",
                "include_in_all" : true
          },
          "nickname":{
            "type": "string",
            "index": "not_analyzed"
        },
        "uin":{
            "type": "long"
        },
        "source_name":{
            "type":"string",
            "index": "not_analyzed"
        },
        "fwdnum":{
            "type": "integer"
        },
        "cmtnum":{
            "type": "integer"
        }
        }
      }
    }
}'
  ```

### RESTful interface:
Most of the RESTful service has `jsonp` support, you just have to add `jsonp=YOUR_CALLBACK` to URLs.

1. Search Service: 


  ```
  GET /search/?q=KEY_WORDS
  GET /search/?q=KEY_WORDS&field=FIELD
  ```
2. Start Server:


  ```
  GET /admin/start/?qq=QQ_NUM&skey=QQ_SKEY
  GET /admin/start/?qq=QQ_NUM&skey=QQ_SKEY&seed=SEED_QQ
  ```
3. Check State:


  ```
  GET /admin/check/
  ```
4. Add Target:


  ```
  GET /admin/addtarget/?qq=QQ_NUM
  ```
5. Add Worker QQ:


  ```
  GET /admin/addworker/?qq=QQ_NUM&skey=QQ_SKEY
  ```
6. Get G_TK:


  ```
  GET /util/getg_tk/?qq=QQ_NUM&skey=QQ_SKEY
  ```
  
  
### Start the Server:
- make sure Elastic Search is running on `localhost`, and install ```oraclejdk8```
- ```sbt run```
- Open a browser(or do it in your application) and do a request like:


  ```
  GET /admin/start/?qq=QQ_NUM&skey=QQ_SKEY
  GET /admin/start/?qq=QQ_NUM&skey=QQ_SKEY&seed=SEED_QQ
  ```
- Check the status and the crawling rate

### Where I can get the skey?

When you logined into Qzone, find the cookies `skey`, skey is started with `@`
