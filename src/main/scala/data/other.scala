package data

import java.time.LocalDate
import java.time.temporal.ChronoUnit.DAYS

class other extends create {

  override def create(): String = {
    val random_weight: String = weight_list(random.nextInt(10)) + ""
    random_weight
  }

  def create_list():String = {
    val AddrName_j = create() match {
      case "0" | "1" | "2" => AddrName_list_j(random.nextInt(48))
      case _ => ""
    }

    val job = create() match {
      case "0" | "1" | "2" => job_list(random.nextInt(13))
      case _ => ""
    }

    val sex = sex_list(random.nextInt(3))

    val birthday = create() match {
      case "0" | "1" | "2" => birthday_create() + ""
      case _ => ""
    }

    val flg_f = create() match {
      case "0" | "1" | "2" => flag_list_one(random.nextInt(4))
      case _ => ""
    }

    val flg_s = create() match {
      case "0" | "1" | "2" => flag_list_two(random.nextInt(2))
      case _ => ""
    }

    val flg_t = create() match {
      case "0" | "1" | "2" => flag_list_three(random.nextInt(3))
      case _ => ""
    }

    val el = create() match {
      case "0" | "1"  => "その他"
      case _ => ""
    }

    val Other:String = "(" + AddrName_j + "," + job + "," + sex + "," + birthday + "," + flg_f + "," + flg_s + "," + flg_t + "," + el + ")"
//    val Other: Array[String] = Array(AddrName_j,job,sex,birthday,flg_f,flg_s,flg_t,el)

    Other
    }

  private lazy val AddrName_list_j: Array[String] = Array(
    "北海道",
    "青森県",
    "岩手県",
    "宮城県",
    "秋田県",
    "山形県",
    "福島県",
    "茨城県",
    "栃木県",
    "群馬県",
    "埼玉県",
    "千葉県",
    "東京都",
    "神奈川県",
    "新潟県",
    "富山県",
    "石川県",
    "福井県",
    "山梨県",
    "長野県",
    "岐阜県",
    "静岡県",
    "愛知県",
    "三重県",
    "滋賀県",
    "京都府",
    "大阪府",
    "兵庫県",
    "奈良県",
    "和歌山県",
    "鳥取県",
    "島根県",
    "岡山県",
    "広島県",
    "山口県",
    "徳島県",
    "香川県",
    "愛媛県",
    "高知県",
    "福岡県",
    "佐賀県",
    "長崎県",
    "熊本県",
    "大分県",
    "宮崎県",
    "鹿児島県",
    "沖縄県",
    "海外",
  )

  private lazy val job_list: Array[String] = Array(
    "教授",
    "芸術",
    "宗教",
    "報道",
    "投資・経営",
    "法律・会計業務",
    "医療",
    "研究",
    "教育",
    "技術",
    "人文知識・国際業務",
    "企業内転勤",
    "興行",
    "技能"
  )

  private lazy val sex_list : Array[String] = Array(
    "男",
    "女",
    "その他"
  )

  private lazy val from = LocalDate.of(1970, 1, 1)
  private lazy val to = LocalDate.of(2010, 1, 1)
  private def birthday_create(): LocalDate = {
    val diff = DAYS.between(from, to)
    from.plusDays(random.nextInt(diff.toInt))
  }

  private lazy val flag_list_one: Array[String] = Array(
    "a",
    "b",
    "c",
    "d",
  )

  private lazy val flag_list_two: Array[String] = Array(
    "1",
    "0",
  )

  private lazy val flag_list_three: Array[String] = Array(
    "b_to_b",
    "b_to_c",
    "p_to_p",
  )

}
