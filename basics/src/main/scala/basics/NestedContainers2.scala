package basics

import cats.data._
import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NestedContainers2 {

  val fa: Future[Option[Int]] = ???
  val fb: Future[Option[Int]] = ???

  // Here, a and b are Int, extracted from both the Future and the Option!
  val finalOptionT = for {
    a <- OptionT(fa)
    b <- OptionT(fb)
  } yield a + b

  val res = finalOptionT.value


}
