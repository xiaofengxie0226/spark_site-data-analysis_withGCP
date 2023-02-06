package data

import java.util.UUID

class userID extends create {
  def create():String ={
    val UserID: String = UUID.randomUUID().toString.replace("-",".").slice(0,13)

    UserID
  }
}
