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

  implicit def rEitherA[A,B,D,G[_,_]](implicit gg: Generic[({type C[X] = G[D,X]})#C], ga: ({type C[X] = G[D,X]})#C[A], gb: ({type C[X] = G[D,X]})#C[B]): ({type C[X] = G[D,X]})#C[Either[A,B]] = {
    gg.view(EitherIso, () => gg.plus(ga,gb))
  }

  implicit def GEitherA[A,B,D,G[_,_]](implicit gg: Generic[({type C[X] = G[D,X]})#C], a: GRep[({type C[X] = G[D,X]})#C,A], b: GRep[({type C[X] = G[D,X]})#C,B]): GRep[({type C[X] = G[D,X]})#C,Either[A,B]] =
    new GRep[({type C[X] = G[D,X]})#C,Either[A,B]] {
      def grep : ({type C[X] = G[D,X]})#C[Either[A,B]] = rEitherA(gg,a.grep,b.grep)
    }


  implicit def rEitherB[A,B,D,G[_,_,_],F](implicit gg: Generic[({type C[X] = G[F,D,X]})#C], ga: ({type C[X] = G[F,D,X]})#C[A], gb: ({type C[X] = G[F,D,X]})#C[B]): ({type C[X] = G[F,D,X]})#C[Either[A,B]] = {
    gg.view(EitherIso, () => gg.plus(ga,gb))
  }

  implicit def GEitherB[A,B,D,G[_,_,_],F](implicit gg: Generic[({type C[X] = G[F,D,X]})#C], a: GRep[({type C[X] = G[F,D,X]})#C,A], b: GRep[({type C[X] = G[F,D,X]})#C,B]): GRep[({type C[X] = G[F,D,X]})#C,Either[A,B]] =
    new GRep[({type C[X] = G[F,D,X]})#C,Either[A,B]] {
      def grep : ({type C[X] = G[F,D,X]})#C[Either[A,B]] = rEitherB(gg,a.grep,b.grep)
    }

}