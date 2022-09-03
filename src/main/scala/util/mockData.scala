package util

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession


object mockData {
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext()
    val sparkSession: SparkSession = SparkSession.builder()
      .appName("mockData")
      .getOrCreate()
    val mock = new generateData()

    for (i<-1 to 5){
      val userOneLog = mock.UserLog()
      val rdd = sc.parallelize(userOneLog)
      val df = sparkSession.createDataFrame(rdd = rdd)
        .toDF("AccessTime","UserID","SessionID","PageID","JobID","JobInfo","ApplyAction")

      df.write.option("compression", "gzip").json(s"gs://user-log-data-uscentral1/user_log_$i")
    }

    sparkSession.stop()
//    val mock = new generateData()
//    val userOneLog = mock.UserLog()
//
//    println(userOneLog)
  }
}
