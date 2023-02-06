package util

import data._

object test {
  def main(args: Array[String]): Unit = {
    val mock = new generateData()
    val userOneLog = mock.UserLog()

    println(userOneLog)


  }
}
