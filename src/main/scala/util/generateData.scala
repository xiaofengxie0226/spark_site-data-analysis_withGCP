package util

import scala.util.Random
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.collection.mutable.ListBuffer

import data._

class generateData {
  /*
  Create UserLog & Schema
  AccessTime: timestamp(2022-10-10 00:00:02)
  UserID: String(2nly14p9gc.1664340236)
  RemoteAddr:String(180.60.160.0)
  IpAddrName: String(tokyo)
  UserAgent:String(Mozilla/5.0 (Linux; Android 12; SC-03L) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Mobile Safari/537.36)
  Referer: String(https://youtube.com/,https://www.google.com/)
  SearchWord: String("齋藤飛鳥")
  Other: StringList(["都道府県(出身地)","職業","性別","生年月日(1993-12-05)","flag1","flag2","flag3","その他１"])
  SessionID: String("24f55150-55c64203")
  PageID: Int("https://example/?s=nogizaka&paged=11")
   */
  private val AccessTime = new accessTime()
  private val UserID = new userID
  private val RemoteAddr = new remoteAddr()
  private val AddrName = new ipAddrName()
  private val UserAgent = new userAgent()
  private val Referer = new referer()
//  private val SessionID = new sessionID()
  private val PageID = new pageID()
  private val SearchWord = new searchWord()
  private val Other = new other()

  def UserLog(): Seq[(String,String,String,String,String,String,String,String,String)] = {
    val random = new Random()
    val log = new ListBuffer[(String,String,String,String,String,String,String,String,String)]
    for(i<- 0 until 10000){
      val remoteaddr = RemoteAddr.create()
      val usergent = UserAgent.create()
      val addrname = AddrName.create()
      val other = Other.create_list()
      val referer = Referer.create()
//        val sessionid = SessionID.create()
      for (k<-0 to random.nextInt(50)){
        val accesstime = AccessTime.create()
        val pageid = PageID.create()
        val searchword = SearchWord.create()
        val recordid = UserID.create()
        val row = (recordid,remoteaddr,usergent,addrname,referer,accesstime,pageid,searchword,other)
        //add to ListBuffer
        log += row
      }
    }
    log
  }
}
