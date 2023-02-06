package util

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

object mockData {
  def main(args: Array[String]): Unit = {
    val times = if (args.length > 0) args(0) else "200"
//    args.foreach(print)
    println(s"${times}回ログデータを作成します")

    lazy val gcs_path:String = "user-log-data-us-central1"

    val sc = new SparkContext()
    val sparkSession: SparkSession = SparkSession.builder()
      .appName("mockData")
      .getOrCreate()
    val mock = new generateData()

    for (i<-1 to times.toInt){
      val userOneLog = mock.UserLog()
      val rdd = sc.parallelize(userOneLog)
      val df = sparkSession.createDataFrame(rdd = rdd)
        .toDF("recordid","remoteaddr","usergent","addrname","referer","accesstime","pageid","searchword","other")

      df.write.option("compression", "gzip").json(s"gs://$gcs_path")
      println(i + " time is over")
    }

    sparkSession.stop()
  }
}
