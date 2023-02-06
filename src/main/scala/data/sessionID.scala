package data

import java.util.UUID

class sessionID extends create {
  def create():String ={
    val SessionID: String = UUID.randomUUID().toString.slice(0,8) + "-" + UUID.randomUUID().toString.slice(0,8)

    SessionID
  }
}
