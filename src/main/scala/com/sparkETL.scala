package com
/*
ETL:
1，session整体比例(分析用户行为分析):
--SessionAnalysis class
session访问时长&访问步长
整个session时间段内各个页面，各个商品，各个搜索关键字，购物篮以及各个成交的情况
--UserPersona class
用户画像与行为联系：用户的性别，年龄，住所与session的各个属性相结合
访问各个页面的情况，顺序
--ProductAnalysis class
商品热门搜索TOP10，购物篮Top10，成交Top10

2,页面转化率（CVR）分析
--session中最终到达结账页面且成交的分析
最后成交的session的分析
页面跳转规律，页面跳转路径
脱离页面统计与脱离率

3，实时成交分析
不断生产成交信息进行在库管理以及价格调整（本次简化所有商品不涉及价格）
实时热门商品
 */

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

object sparkETL {
  def main(args: Array[String]): Unit = {
    //Get GCP Variables
    lazy val project = "sha-dev-356212"
    lazy val dataset = "testdataset"
    //Temporary or persistent GCS bucket must be informed to save in bigquery
    lazy val bucket = "dataproc-uscentral1"

    val conf = new SparkConf().setAppName("SparkSessionAnalysis")
    val sc = new SparkContext(conf)
    val sparkSession: SparkSession = SparkSession.builder()
      .appName("SparkETL")
      .getOrCreate()
    sparkSession.conf.set("temporaryGcsBucket", bucket)


    //master file read
    lazy val connectCloudSQL = new ConnectCloudSQL()
//    lazy val product_info_table = connectCloudSQL.connectTable(filename = "product_info")
    lazy val user_info_table = connectCloudSQL.connectTable(filename = "user_info")
//    lazy val product_info = sc.broadcast(product_info_table)
//    product_info.value.show(10)
    lazy val user_info = sc.broadcast(user_info_table)
//    user_info.value.show(10)

    /*
    session-analysis_1

    SessionAT: Session Active Time
    SessionStep: Session Step Size
     */
    val sa = new SessionAnalysis(sparkSession)
    val SessionTimeAndStep = sa.SessionTimeAndStepCount()

    /*
    session-analysis_2

    PageCount: 各个时间段内的各个页面的访问量 -> hours, minutes, seconds
    ClickCount:点击的商品ID的情况
    SearchWordCount:搜索的关键字（商品名称）
     */

    val PageCount = sa.CountOfColumn("page_id")
    val ClickCount = sa.CountOfColumn("click_product_id")
    val SearchWordCount = sa.CountOfColumn("search_product_name")

    /*
    session-analysis_2

    ProductListCount: product_cnt in bucket or order
    PayCount:付款发生时session,bucket和order的情况
     */
    val ProductListCount = sa.ProductListCount()
    val PayCount = sa.PayCount()

    /*
    session-analysis_3
    session-analysis_4

    UserPersona:
    每日-用户性别-所在区域统计成交数量

    ProductAnalysis:
    ProductInBucketAndOrder:购物篮内的情况&下单情况
     */
    val UserSexAndCity = sa.UserSexAndCity(user_info)
    val UserBucketProducts = sa.UserProducts(user_info,"bucket")
    val UserOrderProducts = sa.UserProducts(user_info,"order")

    /*
    cvr analysis-1
     */
    val ca = new CvrAnalysis(sparkSession)
    val PageSeq = ca.PageSeq(nest_flg = true)
    val PageSeqUnnest = ca.PageSeq(nest_flg = false)

    //write to bigquery
    val tableMap = Map("SessionTimeAndStep"->SessionTimeAndStep
      ,"PageCount"->PageCount
      ,"ClickCount"->ClickCount
      ,"SearchWordCount"->SearchWordCount
      ,"ProductListCount"->ProductListCount
      ,"PayCount"->PayCount
      ,"UserSexAndCity"->UserSexAndCity
      ,"UserBucketProducts"->UserBucketProducts
      ,"UserOrderProducts"->UserOrderProducts
      ,"PageSeq"->PageSeq
      ,"PageSeqUnnest"->PageSeqUnnest
    )
    for ((tableName, table) <- tableMap){
      table.write.format("bigquery")
        .option("table",s"$project.$dataset.$tableName")
        .mode("Overwrite")
        .save()
    }

    /*
    !Not for production environment!

    all Sample logs to Bigquery
     */
//    sa.log_format.write.format("bigquery")
//      .option("table",s"$project.$dataset.ALlLogs")
//      .mode("Overwrite")
//      .save()

    sparkSession.stop()
  }
}
