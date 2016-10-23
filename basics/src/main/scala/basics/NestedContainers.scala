package basics

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class NestedContainers {

  // now we have some async signatures
  def getUserName(data: Map[String, String]): Option[String] = ???
  def getUser(name: String): Future[Option[User]] = ???
  def getEmail(user: User): String = ???
  def validateEmail(email: String): Option[String] = ???
  def sendEmail(email: String): Future[Option[Boolean]] = ???
  val data = Map[String, String]()

  for {
    username <- getUserName(data)
    user <- getUser(username)
    email = getEmail(user)
    validatedEmail <- validateEmail(email)
    success <- sendEmail(email)
  } yield success
}
