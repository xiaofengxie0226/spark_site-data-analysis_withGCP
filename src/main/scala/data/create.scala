package data

import scala.util.Random

trait create {
  protected val random = new Random()
  lazy val weight_list:Array[Int] = Array(0,1,2,3,4,5,6,7,8,9)

  def create():String
}
