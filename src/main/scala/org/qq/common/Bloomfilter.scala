package org.qq.common

/**
  * This file contains a genetic Bloomfilter. This filter can be used as a function: T => Boolean
  * Created by Scott on 12/27/15.
  */
import scala.math.{abs, log}
import scala.util.hashing.MurmurHash3._

class Bloomfilter[T] (val k: Int,
                      private val content: Array[Boolean]
                     ) extends(T => Boolean){
  var count = 0
  val array_size = content.length
  content.transform(_ => false)
  val seed_hash = (seed:Int, item:T) => abs(stringHash(item.toString,seed)) % array_size + 1

  def apply(x:T):Boolean = contains(x)

  def contains(x:T): Boolean = {
    val is_filtered = (1 to k).forall(i => content(seed_hash(i,x)))
    if(!is_filtered)
      (1 to k).foreach(i => content(seed_hash(i,x)) = true)
    is_filtered
  }
}

object Bloomfilter{
  /**
    *
    * @param filter_size: size of the filter
    * @param hash_num: number of hash functions
    * @tparam T: the type you want to filter
    * @return
    */
  def apply[T](filter_size:Int, hash_num: Int):Bloomfilter[T] =
  {
    val content = new Array[Boolean](filter_size)
    val bloomfilter = new Bloomfilter[T](hash_num,content)
    bloomfilter
  }

  /**
    *
    * @param elements_num: the approximate num of elements in the bloomfilter
    * @param false_positive the estimate false_positive rate
    * @return a tuple of 2 int, first one is size of bitarray, second is the number of hash functions
    */
  def calculate_size_k_optimal(elements_num:Int,false_positive:Double): (Int,Int) ={
    val m:Int = ( -elements_num*log(false_positive) / (log(2)*log(2)) + 0.5).toInt
    val k:Int = ((m/elements_num) * log(2) + 0.5).toInt
    (m,k) //(filter_size, hash fucntion number)
  }

  /**
    * create a Bloomfilter that is optimal
    * @param element_num estimated element number
    * @param false_positive goal of false_positive
    * @tparam T type to filter
    * @return a new Bloomfilter
    */
  def optimal_filter[T](element_num:Int, false_positive:Double):Bloomfilter[T] = {
    val (filter_size, hash_num) = calculate_size_k_optimal(element_num,false_positive)
    apply[T](filter_size,hash_num)
  }
}
