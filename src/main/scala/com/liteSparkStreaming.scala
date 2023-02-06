//package com
//
//import org.apache.spark.sql.SparkSession
//
//object liteSparkStreaming {
//  def main(args: Array[String]): Unit = {
//    lazy val bucket = "dataproc-uscentral1"
//
//    val sparkSession: SparkSession = SparkSession.builder()
//      .appName("SparkStreaming")
//      .getOrCreate()
//
//    //read structure stream from pubsub-lite as df
//    val df = sparkSession.readStream
//    .format("pubsublite")
//    .option("pubsublite.subscription", "projects/271982357043/locations/us-central1-b/subscriptions/ad-platSub")
//    .load()
//
//    //check df
//    df.show()
//
//    //write into Bigquery
////    val query = df.writeStream
////    .format("bigquery")
////    .option("temporaryGcsBucket",bucket)
////    .option("checkpointLocation", "some-location") //must set a check point like gs://checkpoint-bucket/checkpointDir
////    .option("table", "dataset.table")
////      .outputMode("append").start()
//
//    //query start
////    query.awaitTermination(120)
////    query.stop()
//  }
//}
