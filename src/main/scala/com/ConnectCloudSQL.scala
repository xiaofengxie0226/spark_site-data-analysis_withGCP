//package com
//
//import org.apache.spark.sql
//import org.apache.spark.sql.SparkSession
//
//class ConnectCloudSQL {
//  val sparkSession: SparkSession = SparkSession.builder().appName("Connect-CloudSQL").getOrCreate()
//  //    "jdbc:mysql://104.198.140.42:3306/comp_db?user=root&password=password"
//  //    "jdbc:mysql:///comp_db?cloudSqlInstance=sha-dev-356212:us-central1:company-db&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=password"
//  val url = "jdbc:mysql://104.198.140.42:3306/comp_db?user=root&password=password"
//
//  def connectTable(filename: String): sql.DataFrame = {
//    val table = sparkSession.read.format("jdbc")
//      .options(Map(
//        "url" -> url,
//        "dbtable" -> filename
//      ))
//      .load()
//    table
//  }
//
//
//}
