package basics

import cats.data._
import cats.implicits._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future


class NestedContainers3 {

  // now we have some async signatures
  def getUserName(data: Map[String, String]): Option[String] = ???
  def getUser(name: String): Future[Option[User]] = ???
  def getEmail(user: User): String = ???
  def validateEmail(email: String): Option[String] = ???
  def sendEmail(email: String): Future[Option[Boolean]] = ???
  val data = Map[String, String]()


  for {
    username <- OptionT.fromOption[Future](getUserName(data))
    user <- OptionT(getUser(username))
    email = getEmail(user)
    validatedEmail <- OptionT.fromOption[Future](validateEmail(email))
    success <- OptionT(sendEmail(email))
  } yield success

}
