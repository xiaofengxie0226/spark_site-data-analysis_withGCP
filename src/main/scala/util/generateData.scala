package util

import scala.util.Random
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.collection.mutable.ListBuffer

class generateData {
  val random = new Random()
  val pageList: Array[Int] = Array(1,2,3,4,5,6,7)
  /*
  Create UserLog & Schema
  AccessTime: timestamp
  UserID: String
  SessionID: String
  PageID: Int
  JobID: String
  JobInfo: Array
  ApplyAction: 0/1
   */
  def UserLog(): Seq[(String,String,String,String,String,String,String)] = {
    val log = new ListBuffer[(String,String,String,String,String,String,String)]
    for(i<- 0 until 100000){
      //UserID example: M717
      val userID = "M" + random.nextInt(100000)
      for(j<-0 until 10){
        val sessionID = UUID.randomUUID().toString.replace("-","")
        for (k<-0 to random.nextInt(30)){
          val now = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .format(LocalDateTime.now().plusSeconds(random.nextInt(3600)))
          val pageID = pageList(random.nextInt(7)) + ""
          val jobID = "M" + random.nextInt(10000)
          val jobInfo = UUID.randomUUID().toString.split("-")(0)
          val applyAction = if(pageID == "7") "1" else "0"
          val row = (now,userID,sessionID,pageID,jobID,jobInfo,applyAction)
          //add to ListBuffer
          log += row
        }
      }
    }
    log
  }
}
