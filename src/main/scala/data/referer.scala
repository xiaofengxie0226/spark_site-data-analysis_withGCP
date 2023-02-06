package data

class referer extends create {
  override def create(): String = {
    val random_weight: Int = weight_list(random.nextInt(10))
    val Referer = random_weight match {
      case 0 | 1 | 2  => referer_list(random.nextInt(8))
      case _ => ""
    }
    Referer
  }

  private lazy val referer_list:Array[String] = Array(
    "https://www.google.com/",
    "https://www.google.co.jp/",
    "https://ads.as.criteo.com/",
    "https://www.bing.com/",
    "https://googleads.g.doubleclick.net/",
    "https://youtube.com/",
    "https://www.facebook.com/",
    "https://twitter.com/",
    "https://www.yahoo.co.jp/",
  )
}
