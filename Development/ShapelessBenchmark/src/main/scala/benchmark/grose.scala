package benchmark

/*
 * data GRose f a = GNode a (f (GRose f a))
 */

sealed trait GRose[F[_],A]

case class GNode[F[_],A](v : A, r : F[GRose[F,A]]) extends GRose [F,A]
