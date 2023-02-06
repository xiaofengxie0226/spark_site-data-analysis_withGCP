package data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class accessTime extends create{
  override def create():String = {
    val AccessTime: String = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
      .format(LocalDateTime.now().plusSeconds(random.nextInt(86400)))
    AccessTime
  }

}
