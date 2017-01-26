package Data

import Base._

object Either {
  type EitherRep[A,B] = Plus[A,B]

  def EitherIso[A,B] : Iso[Either[A,B],EitherRep[A,B]] = new Iso[Either[A,B],EitherRep[A,B]] {
    def fromEither[A,B](l : Either[A,B]) : EitherRep[A,B] = l match {
      case Left(x)  => Inl(x)
      case Right(x) => Inr(x)
    }
    def toEither[A,B](r : EitherRep[A,B]) : Either[A,B] = r match {
      case Inl(x) => Left(x)
      case Inr(x) => Right(x)
    }
    def from = fromEither
    def to   = toEither
  }

  implicit def rEither[A,B,G[_]](implicit gg: Generic[G], ga: G[A], gb: G[B]): G[Either[A,B]] = {
    gg.view(EitherIso, () => gg.plus(ga,gb))
  }

  implicit def GEither[A,B,G[_]](implicit gg: Generic[G], a: GRep[G,A], b: GRep[G,B]): GRep[G,Either[A,B]] =
    new GRep[G,Either[A,B]] {
      def grep : G[Either[A,B]] = rEither(gg,a.grep,b.grep)
    }

}