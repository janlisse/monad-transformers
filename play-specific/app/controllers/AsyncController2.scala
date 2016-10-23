package controllers

import javax.inject._
import cats.implicits._

import akka.actor.ActorSystem
import cats.data.{Xor, XorT}
import play.api.mvc._
import services.UserService
import HttpResultTransformer._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class AsyncController2 @Inject()(actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller {


  def index = Action.async { request =>
    val data = request.queryString.mapValues(_.head)

    val result = for {
      username <- Xor.fromOption(UserService.getUserName(data), BadRequest("Username missing from request")) |> Future.successful |> XorT.apply
      user <- XorT.apply(UserService.getUser(username).map(Xor.fromOption(_, NotFound("User not found"))))
      email = UserService.getEmail(user)
      validatedEmail <- UserService.validateEmail(email).leftMap(InternalServerError(_)) |> Future.successful |> XorT.apply
      success <- UserService.sendEmail(validatedEmail).map(Xor.right[Result, Boolean]) |> XorT.apply
    } yield {
      if (username == "billo") Ok("Mail successfully sent!")
      else InternalServerError("Failed to send email :(")
    }

    result.fold(identity, identity)

  }
}
