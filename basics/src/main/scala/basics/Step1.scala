package basics


class Step1 {

  def getUserName(data: Map[String, String]): Option[String] = ???
  def getUser(name: String): Option[User] = ???
  def getEmail(user: User): String = ???
  def validateEmail(email: String): Option[String] = ???
  def sendEmail(email: String): Option[Boolean] = ???
  val data = Map[String, String]()

  val result1 = getUserName(data).flatMap { username =>
    getUser(username).flatMap { user =>
      val email = getEmail(user)
      validateEmail(email).flatMap { validatedEmail =>
        sendEmail(validatedEmail)
      }
    }
  }
}
