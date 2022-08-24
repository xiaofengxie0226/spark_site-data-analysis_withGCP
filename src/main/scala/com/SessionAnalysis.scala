package com

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

class SessionAnalysis(sparkSession: SparkSession,val unit_of_time: List[String]) {
//  get_variables
  val gcs_path = "gs://logdata_uscentral1/"
  val log_format: DataFrame = ReadLogFile()
  val Log_whole: DataFrame = AllSessionAnalysis()

  //read all json.gz files into a DataFrame and tidy format
  def ReadLogFile(): DataFrame = {
    val log = sparkSession.read.json(gcs_path)
    val log_format = log.withColumn("action_time_string",concat_ws("",col("action_time")))
      .withColumn("action_time_format",to_timestamp(col("action_time_string")))
      .withColumn("pay_time_string",concat_ws("",col("pay_time")))
      .withColumn("pay_time_format",to_timestamp(col("pay_time_string")))
      .select("user_id", "session_id"
        ,"action_time_format","page_id","search_product_name"
        ,"click_product_id","bucket_list","order_list","pay_time_format")
    log_format
  }


  /*
  session-analysis_1

  session访问时长：session_id + action_time + max&min
  session访问步长：user_id + session_id + count
  */
  def SessionTimeAndStepCount(): DataFrame = {
    //val trimstring = "[] "
    //df.withColumn("action_time_trim",trim(col("action_time"),trimstring))
    val SessionTimeAndStep = log_format.groupBy("session_id","user_id")
      .agg(max("action_time_format") as "maxTime"
        ,min("action_time_format") as "minTime"
        ,count("session_id") as "SessionLength")
      .withColumn("ActiveTimeOnSecond"
        ,col("maxTime").cast("long")-col("minTime").cast("long"))
      .orderBy("user_id","session_id")

    SessionTimeAndStep
  }


  /*
  session-analysis_2

  AllSessionAnalysis:时间段整理
   */
  def AllSessionAnalysis():DataFrame ={
    lazy val AllSessionLog = log_format.withColumn("YYYYMMDD",to_date(col("action_time_format")))
    val Log_whole = this.unit_of_time.length match {
      case 1 => AllSessionLog.withColumn("hour", hour(col("action_time_format")))
      case 2 => AllSessionLog.withColumn("hour", hour(col("action_time_format")))
        .withColumn("minute", minute(col("action_time_format")))
      case 3 =>AllSessionLog.withColumn("hour", hour(col("action_time_format")))
        .withColumn("minute", minute(col("action_time_format")))
        .withColumn("second", second(col("action_time_format")))
    }

    Log_whole
  }
  /*
  session-analysis_2

  PageCount: 各个时间段内的各个页面的访问量 -> hours, minutes, seconds
  ClickCount:点击的商品ID的情况
  SearchWordCount:搜索的关键字（商品名称）
   */
  def CountOfColumn(column: String):DataFrame ={
    val countOfColumn = column match {
      case "page_id" => Log_whole
        .groupBy("YYYYMMDD","page_id","hour","minute")
        .agg(count("page_id") as "pageAccessCount")
      case "click_product_id" => Log_whole
        .groupBy("YYYYMMDD","page_id","click_product_id","hour","minute")
        .agg(count("click_product_id") as "ClickProductCount")
      case "search_product_name" => Log_whole.filter(Log_whole("search_product_name") =!= "null")
        .groupBy("YYYYMMDD","page_id","search_product_name","hour","minute")
        .agg(count("search_product_name") as "SearchWordCount")
    }

    countOfColumn.orderBy("YYYYMMDD","hour","minute")
  }

  /*
  session-analysis_2

  ProductListCount: product_cnt in bucket or order
  PayCount:付款发生时session,bucket和order的情况
   */
  def ProductListCount(): DataFrame = {
    val productList = Log_whole.withColumn("bucketListLength", size(col("bucket_list")))
      .withColumn("OrderListLength", size(col("order_list")))
      .groupBy("YYYYMMDD","user_id","hour","minute")
      .agg(sum("bucketListLength") as "bucketListSum"
        ,sum("OrderListLength") as "OrderListSum")

    productList
  }

  def PayCount():DataFrame={
  /*
  pay_time is empty
  when page = 5 and order_list is not empty that means
  pay success
   */
    lazy val payCount_add = Log_whole
      .withColumn("OrderListLength", size(col("order_list")))
    val payCount = payCount_add
      .filter(payCount_add("page_id") === 5 && payCount_add("OrderListLength") > 0 )
      .select("user_id","session_id","page_id","action_time_format","order_list")

    payCount
  }


  /*
  session-analysis_3
  session-analysis_4

  UserPersona:
  每日-用户性别-所在区域统计成交数量
  同一个Session里不同时间段，以及不同Session的购入的各个商品数量

  ProductAnalysis:
  TopProductInBucketAndOrder:热门商品Top--in bucket and in order
   */
  def UserSexAndCity(user_info_master: Broadcast[DataFrame]): DataFrame = {
    lazy val user_info = user_info_master.value
    lazy val userJoin = Log_whole.join(user_info,Log_whole("user_id") === user_info("user_id"),"left")
      .withColumn("OrderListLength", size(col("order_list")))
    val userSexAndCity = userJoin
      .filter(userJoin("page_id") === 5 && userJoin("OrderListLength") > 0 )
      .groupBy("sex_id","city_id","YYYYMMDD")
      .agg(sum("OrderListLength") as "SumOfOrderProduct")

    userSexAndCity
  }

  def UserBucketProducts(user_info_master: Broadcast[DataFrame]): DataFrame = {
    lazy val userInfo = user_info_master.value
    lazy val userJoin = Log_whole.join(userInfo,Log_whole("user_id") === userInfo("user_id"),"left")
      .withColumn("bucketListLength", size(col("bucket_list")))
    val userBucketProducts = userJoin
      .filter(userJoin("bucketListLength") > 0 )
      .select(userJoin("sex_id"),userJoin("city_id"),userJoin("YYYYMMDD")
        ,explode(userJoin("bucket_list")) as "product_id")
      .groupBy("product_id","sex_id","city_id","YYYYMMDD").count()

    userBucketProducts
  }

  def UserOrderProducts(user_info_master: Broadcast[DataFrame]): DataFrame = {
    lazy val userInfo = user_info_master.value
    lazy val userJoin = Log_whole.join(userInfo,Log_whole("user_id") === userInfo("user_id"),"left")
      .withColumn("OrderListLength", size(col("order_list")))
    val userOrderProducts = userJoin
      .filter(userJoin("page_id") === 5 && userJoin("OrderListLength") > 0 )
      .select(userJoin("sex_id"),userJoin("city_id"),userJoin("YYYYMMDD")
        ,explode(userJoin("order_list")) as "product_id")
      .groupBy("product_id","sex_id","city_id","YYYYMMDD").count()

    userOrderProducts
  }

}
