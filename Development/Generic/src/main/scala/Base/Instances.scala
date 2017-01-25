package Base

object Instances {

  implicit def ListIso[A] = new Iso[List[A], Plus[Unit, Product[A, List[A]]]] {
    def fromList[A]: (List[A] => Plus[Unit, Product[A, List[A]]]) = l =>
      l match {
        case Nil => Inl(Unit)
        case (x :: xs) => Inr(Product(x, xs))
      }

    def toList[A]: (Plus[Unit, Product[A, List[A]]] => List[A]) = r =>
      r match {
        case Inl(_) => Nil
        case Inr(Product(x, xs)) => x :: xs
      }

    def from = fromList
    def to   = toList
  }

  implicit def EitherIso[A, B] = new Iso[Either[A, B], Plus[A, B]] {
    def from: (Either[A, B] => Plus[A, B]) = e => e match {
      case Left(x) => Inl(x)
      case Right(x) => Inr(x)
    }
    def to: (Plus[A, B] => Either[A, B]) = p => p match {
      case Inl(x) => Left(x)
      case Inr(x) => Right(x)
    }
  }
}
