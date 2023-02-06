package data

class searchWord extends create {
  override def create(): String = {
    val random_weight: Int = weight_list(random.nextInt(10))
    val SearchWord = random_weight match {
      case 0 | 1 | 2 => keyword_list(random.nextInt(20))
      case _ => ""
    }
    SearchWord
  }

  private lazy val keyword_list: Array[String] = Array(
    "SDGs",
    "DX",
    "イベント",
    "新商品",
    "キャンペーン",
    "期間限定",
    "スタートアップ",
    "EC",
    "プレゼント",
    "AI",
    "オンライン",
    "スイーツ",
    "グルメ",
    "コラボ",
    "マーケティング",
    "ホテル",
    "サスティナブル",
    "ファッション",
    "ゲーム",
    "ギフト",
  )
}
