package com

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window


class CvrAnalysis(sparkSession: SparkSession) extends LoadData(sparkSession: SparkSession){
  /*
  cvr analysis-1
  schema: session_id ,user_id, read_page_sequence, pay_cnt(page == 5)

  cvr analysis-2
  schema: unnest read_page_sequence to column (in order to ML)
   */
  def PageSeq(nest_flg: Boolean): DataFrame = {
    // create pay flg
    lazy val pageSeqOne = log_format.withColumn("pay_flg"
      , when(col("page_id") === 5, 1).otherwise(0))
    //to keep sequence of page_visit, using Window function
    lazy val w = Window.partitionBy("session_id","user_id").orderBy("action_time_format")
    val pageSeqTwo = pageSeqOne
      .withColumn("page_list_sorted", collect_list("page_id").over(w))
    // keep max list of page_visit and then agg
    val pageSeq = pageSeqTwo
      .groupBy("session_id", "user_id")
      .agg(max("page_list_sorted") as "PageReadSequence"
        , sum("pay_flg") as "pay_times_cnt") //convert row to list type column
    val pageSequence = if (nest_flg) {
      pageSeq
    } else {
      pageSeq.select(
        pageSeq("session_id") +: pageSeq("user_id") +: pageSeq("pay_times_cnt") +:
          //unnest sequence to column and the SessionSepLength is decided by ML model
          (0 to 49).map(i => pageSeq("PageReadSequence")(i).alias(s"PageVisit_$i")): _*
      )
    }
    pageSequence
  }

}
