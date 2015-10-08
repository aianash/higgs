package higgs.core.patterns

class Chain[A, B](f: A => Either[A, B]) {

  def or(next: => (A => Either[A, B])): A => Either[A, B] =
    f(_).left.flatMap(next)

  def +>(next: => (A => Either[A, B])): A => Either[A, B] = or(next)
}

object Chain {

  implicit def func2chain[A, B](f: A => Either[A, B]) =
    new Chain[A, B](f)

}