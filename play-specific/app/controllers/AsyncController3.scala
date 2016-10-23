package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import cats.implicits._
import controllers.HttpResultTransformer._
import play.api.mvc.{Action, Controller}
import services.UserService

import scala.concurrent.ExecutionContext

@Singleton
class AsyncController3 @Inject()(actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller {

  import HttpResult._

  def index = Action.async { request =>
    val data = request.queryString.mapValues(_.head)

    for {
      username <- UserService.getUserName(data) |> fromOption(BadRequest("Username missing from request"))
      user <- UserService.getUser(username) |> fromFOption(NotFound("User not found"))
      email = UserService.getEmail(user)
      validatedEmail <- UserService.validateEmail(email)|> fromXorWithLeft(BadRequest(_))
      success <- UserService.sendEmail(email) |> fromFuture
    } yield {
      if(success) Ok("Mail successfully sent!")
      else InternalServerError("Failed to send email :(")
    }
  }
}
