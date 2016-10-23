package controllers

import javax.inject._

import akka.actor.ActorSystem
import play.api.mvc._
import services.UserService

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class AsyncController1 @Inject()(actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller {


  def index = Action.async { request =>
    val data = request.queryString.mapValues(_.head)

    UserService.getUserName(data).map { username =>
      UserService.getUser(username).flatMap {
        case None => Future.successful(NotFound("User not found"))
        case Some(user) =>
          val email = UserService.getEmail(user)
          UserService.validateEmail(email).bimap(
            validatedEmail => {
              UserService.sendEmail(validatedEmail) map {
                case true => Ok("Mail successfully sent!")
                case false => InternalServerError("Failed to send email :(")
              }
            },
            errorMsg => Future.successful(InternalServerError(errorMsg))).fold(identity, identity) // This is annoying. We can do this with 'merge' in Scalaz 7.1
      }
    } getOrElse Future.successful(BadRequest("Username missing from data!"))
  }


}
