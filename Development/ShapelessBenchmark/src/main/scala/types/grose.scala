package types

/*
 * data GRose f a = GNode a (f (GRose f a))
 */

final case class GRose[F[_],A](v : A, r : F[GRose[F,A]])
