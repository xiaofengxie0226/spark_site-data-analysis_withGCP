package data


class remoteAddr extends create {
  def create():String = {
    val RemoteAddr = random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255)

    RemoteAddr
  }
}
