package services


import cats.data.Xor

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object UserService {

  case class User(name: String, email: String)

  private val users = Seq(
    User("eamelink", "erik@lunatech.com"),
    User("pedro", "peter@lunatech.com"),
    User("sietse", "targeter"),
    User("paco", "paco@example.com"))

  /**
    * Extract the username from a Map with data
    */
  def getUserName(data: Map[String, String]): Option[String] =
    data.get("username")

  /**
    * Find the user with a given username.
    */
  def getUser(name: String): Future[Option[User]] =
    Future(users.find(_.name == name))

  /**
    * Get the email address of a user
    */
  def getEmail(user: User): String = user.email

  /**
    * Validate an email address.
    * Returns a -\/ with an error message if `email` is not a valid email address,
    * or a copy of the email address if it is.
    */
  def validateEmail(email: String): String Xor String =
    if (email contains "@")
      Xor.right(email)
    else
      Xor.left("Not a valid email address: " + email)

  /**
    * Send an email.
    * Returns true if successful, false otherwise
    */
  def sendEmail(email: String): Future[Boolean] =
    Future.successful(
      if (email endsWith "example.com") false
      else true)


}
