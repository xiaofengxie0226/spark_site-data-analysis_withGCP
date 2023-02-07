package com

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

class LoadData(sparkSession: SparkSession, useragent_os_info: Broadcast[DataFrame]) {
  //  get_variables
  lazy val gcs_path = s"gs://user-log-data-us-central1/weblog/user_log_*/*.json.gz"
  //LoadData
  lazy val log_format: DataFrame = ReadLogFile()
  lazy val userLogJoin:DataFrame = UserLogJoin()

  //read all json.gz files into a DataFrame and tidy format
  /*
  データクリーニング：
  1,AccessTime: timestamp(2022-10-10 00:00:02) :StringからTimeStampに変換
  2,RecordID: String(2nly14p9gc.1664)　：削除
  3,RemoteAddr:String(180.60.160.0)　：保留、group by用のkey、ユーザIDとして使用
  →ユーザでの完全識別はやや無理なので、同じIP＋OSで一人のユーザとして認定します。
  4,IpAddrName: String(tokyo)　：保留
  5,UserAgent:String(Mozilla/5.0 (Linux; Android 12; SC-03L) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Mobile Safari/537.36)
  ：OS情報だけ抽出、useragent_os_info対照表（Bigquery上にあるマスターテーブル：useragent_os_info）に参照して結合
  ：ユーザIDとして使用
  6,Referer: String(https://youtube.com/,https://www.google.com/)　：保留
  7,SearchWord: String("齋藤飛鳥")　：保留
  8,Other: StringList(["都道府県(出身地)","職業","性別","生年月日(1993-12-05)","flag1","flag2","flag3","その他１"])
  ：展開、別テーブルとして作成（userid,birthplace,job,sex,birthday,flg1,flg2,flg3,else）
  9,PageID: Int("https://example/?s=nogizaka&paged=11")　：pageIDだけ抽出

  Bigqueryへの書き込み：
  テーブル１：UserInfo
  テーブル２：UserLog
   */

  def ReadLogFile(): DataFrame = {
    val log = sparkSession.read.json(gcs_path)
    val log_format = log.withColumn("Access_time",to_timestamp(col("access_time")))
      .withColumn("PageID",split(col("page_id"),"=").getItem(2))
      .select("remoteIP", "userAgent"
        ,"IpAddress","referer","Access_time"
        ,"PageID","search_word","other")
    log_format
  }

  def UserLogJoin(): DataFrame = {
    lazy val osinfo = useragent_os_info.value
    val UserLogJoin = log_format.join(osinfo,log_format("userAgent") === osinfo("userAgent"),"left")
      .withColumn("userID",concat(col("remoteIP"),lit('-'),
        col("os")))
    UserLogJoin
    }

  def UserInfo():DataFrame ={
    val UserInfo = userLogJoin.withColumn("birthplace",
      split(col("other"),",").getItem(0))
      .withColumn("job",split(col("other"),",").getItem(1))
      .withColumn("sex",split(col("other"),",").getItem(2))
      .withColumn("birthday",split(col("other"),",").getItem(3))
      .withColumn("flg1",split(col("other"),",").getItem(4))
      .withColumn("flg2",split(col("other"),",").getItem(5))
      .withColumn("flg3",split(col("other"),",").getItem(6))
      .withColumn("else",split(col("other"),",").getItem(7))
      .select("userID","birthplace","job","sex","birthday","flg1","flg2","flg3","else")
      .groupBy("userID","birthplace","job","sex","birthday","flg1","flg2","flg3","else")
      .agg(count("userID") as "StepsCount").orderBy("userID")
    UserInfo
  }

  def UserLog():DataFrame ={
    val UserLog = userLogJoin.select("remoteIP", "os"
      ,"IpAddress","referer","Access_time"
      ,"PageID","search_word").orderBy("remoteIP","Access_time")
    UserLog
  }
}
