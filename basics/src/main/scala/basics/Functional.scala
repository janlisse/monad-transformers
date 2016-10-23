package basics

object Functional {

  sealed class IdOps[A](self: A) {
    final def |>[B](f: A => B): B =
      f(self)
  }

  implicit def ToIdOps[A](a: A): IdOps[A] = new IdOps(a)

}
