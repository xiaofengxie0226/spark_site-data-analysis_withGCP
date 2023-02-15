package util

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

object mockData {
  def main(args: Array[String]): Unit = {
    val times = if (args.length > 0) args(0) else "200"
//    args.foreach(print)
    println(s"${times}回ログデータを作成します")
//change to your bucket name
    lazy val gcs_path:String = "user-log-data-us-east1/weblog"

    val sc = new SparkContext()
    val sparkSession: SparkSession = SparkSession.builder()
      .appName("mockData")
      .getOrCreate()
    val mock = new generateData()

    for (i<-1 to times.toInt){
      val userOneLog = mock.UserLog()
      val rdd = sc.parallelize(userOneLog)
      val df = sparkSession.createDataFrame(rdd = rdd)
        .toDF("recordID","remoteIP","userAgent","IpAddress","referer","access_time","page_id","search_word","other")

      df.write.option("compression", "gzip").json(s"gs://$gcs_path/user_log_$i")
      println(i + " time is over")
    }

    sparkSession.stop()
  }
}
