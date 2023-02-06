package data

class pageID extends create {
  override def create(): String = {
    val PageID = "https://example/?s=nogizaka&paged=" + random.nextInt(25)
    PageID
  }
}
