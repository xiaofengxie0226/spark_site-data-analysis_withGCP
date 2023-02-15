package data

import java.util.UUID

class recordID extends create {
  def create():String ={
    val recordID: String = UUID.randomUUID().toString.replace("-",".").slice(0,13)

    recordID
  }
}
