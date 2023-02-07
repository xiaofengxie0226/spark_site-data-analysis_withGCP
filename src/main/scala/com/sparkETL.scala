package com
/*
ETL:
０，GCS to Bigquery

１，ユーザ分析
性別、年齢、出身地、現在地、職業

２，トレンド分析
検索キーワード、ページ

３，session分析

 */

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

object sparkETL {
  def main(args: Array[String]): Unit = {
    //Get GCP Variables
    lazy val project = "sinkcapital-001"
    lazy val dataset = "web_log"
    //Temporary or persistent GCS bucket must be informed to save in bigquery
    lazy val bucket = "dataproc-to-bigquery-us-central1"

    val conf = new SparkConf().setAppName("SparkSessionAnalysis")
    val sc = new SparkContext(conf)
    val sparkSession: SparkSession = SparkSession.builder()
      .appName("SparkETL")
      .getOrCreate()
    sparkSession.conf.set("temporaryGcsBucket", bucket)


    //master file read from Bigquery
    lazy val useragent_os_info_table = sparkSession.read.format("bigquery").load(s"$project.$dataset.useragent_os_info")
    lazy val useragent_os_info = sc.broadcast(useragent_os_info_table)
//    user_info.value.show(10)

    //ETL
    val sa = new LoadData(sparkSession, useragent_os_info)
    val UserInfo = sa.UserInfo()
    val UserLog = sa.UserLog()


    //write to bigquery
    val tableMap = Map("UserInfo"->UserInfo
      ,"UserLog"->UserLog
    )
    for ((tableName, table) <- tableMap){
      table.write.format("bigquery")
        .option("table",s"$project.$dataset.$tableName")
        .mode("Overwrite")
        .save()
    }

    sparkSession.stop()
  }
}
