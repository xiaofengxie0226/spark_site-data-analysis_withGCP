package com

import org.apache.spark.sql._
import org.apache.spark.sql.functions._

class LoadData(sparkSession: SparkSession) {
  //  get_variables
  lazy val gcs_path = "gs://logdata_uscentral1/*.json.gz"
  lazy val unit_of_time: Seq[String] = List("hour","minute")
  //LoadData
  lazy val log_format: DataFrame = ReadLogFile()
  lazy val Log_whole: DataFrame = AllSessionAnalysis()

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
  main for session analysis

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
}
