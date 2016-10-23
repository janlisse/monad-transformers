package controllers

import cats.implicits._
import cats.data.{NonEmptyList, Validated, Xor, XorT}
import play.api.data.Form
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsPath, JsResult}
import play.api.mvc.Result

import scala.concurrent.{ExecutionContext, Future}

object HttpResultTransformer {

  type HttpResult[A] = XorT[Future, Result, A]
  type JsonValidationError = Seq[(JsPath, Seq[ValidationError])]

  object HttpResult {
    def point[A](a: A): HttpResult[A] = XorT(Future.successful(Xor.right(a)): Future[Result Xor A])
    def fromFuture[A](fa: Future[A])(implicit ec: ExecutionContext): HttpResult[A] = XorT(fa.map(Xor.right): Future[Result Xor A])
    def fromFutureWithRecover[A](recover: => Result)(fa: Future[A])(implicit ec: ExecutionContext): HttpResult[A] = XorT(fa.map(Xor.right).recover(PartialFunction((t: Throwable) => recover) andThen Xor.left): Future[Result Xor A])
    def fromEither[A, B](failure: B ⇒ Result)(e: Either[B, A]): HttpResult[A] = fromXorWithLeft(failure)(Xor.fromEither(e))
    def fromXor[A](va: Result Xor A): HttpResult[A] = XorT(Future.successful(va))
    def fromXorWithLeft[A, B](left: B ⇒ Result)(va: B Xor A): HttpResult[A] = XorT(Future.successful(va.leftMap(left)))
    def fromOption[A](none: => Result)(oa: Option[A]): HttpResult[A] = XorT(Future.successful(Xor.fromOption(oa, none)))
    def fromForm[A](failure: Form[A] ⇒ Result)(fo: Form[A]): HttpResult[A] = fromXorWithLeft(failure)(fo.toXor)
    def fromJsResult[A](failure: JsonValidationError ⇒ Result)(jsResult: JsResult[A]): HttpResult[A] = fromEither(failure)(jsResult.asEither)
    def fromValidated[A, B](failure: NonEmptyList[B] => Result)(result: Validated[NonEmptyList[B], A]): HttpResult[A] = XorT(Future.successful(result.toXor.leftMap(failure)))
    def fromFOption[A](none: => Result, recover: => Option[Result] = None)(foa: Future[Option[A]])(implicit ec: ExecutionContext): HttpResult[A] =
      if (recover.isDefined) XorT(foa.map(Xor.fromOption(_, none)).recover(PartialFunction((t: Throwable) => recover.get) andThen Xor.left)) else XorT(foa.map(Xor.fromOption(_, none)))
    def fromFEither[A, B](left: B ⇒ Result, recover: => Option[Result] = None)(fva: Future[B Xor A])(implicit ec: ExecutionContext): HttpResult[A] =
      if (recover.isDefined) XorT(fva.map(_.leftMap(left)).recover(PartialFunction((t: Throwable) => recover.get) andThen Xor.left)) else XorT(fva.map(_.leftMap(left)))
    def fromFutureValidated[A, B](failure: NonEmptyList[B] => Result, recover: => Option[Result] = None)(result: Future[Validated[NonEmptyList[B], A]])(implicit ec: ExecutionContext): HttpResult[A] =
      if (recover.isDefined) XorT(result.map(_.toXor.leftMap(failure)).recover(PartialFunction((t: Throwable) => recover.get) andThen Xor.left)) else XorT(result.map(_.toXor.leftMap(failure)))
  }

  sealed class IdOps[A](self: A) {
    final def |>[B](f: A => B): B =
      f(self)
  }

  sealed class FormOps[A](self: Form[A]) {
    final def toXor: Xor[Form[A], A] = self.fold(
      errors => Xor.left(errors),
      success => Xor.right(success)
    )
  }

  implicit def ToResult(r: HttpResult[Result])(implicit ec: ExecutionContext): Future[Result] = r.fold(identity, identity)

  implicit def ToIdOps[A](a: A): IdOps[A] = new IdOps(a)

  implicit def ToFormOps[A](f: Form[A]): FormOps[A] = new FormOps(f)

}
