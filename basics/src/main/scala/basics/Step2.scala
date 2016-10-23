package basics

class Step2 {

  def getUserName(data: Map[String, String]): Option[String] = ???
  def getUser(name: String): Option[User] = ???
  def getEmail(user: User): String = ???
  def validateEmail(email: String): Option[String] = ???
  def sendEmail(email: String): Option[Boolean] = ???
  val data = Map[String, String]()

  for {
    username <- getUserName(data)
    user <- getUser(username)
    email = getEmail(user)
    validatedEmail <- validateEmail(email)
    success <- sendEmail(email)
  } yield success

}
