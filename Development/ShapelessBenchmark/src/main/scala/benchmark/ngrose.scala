package benchmark

/*
 * data NGRose f a
 *   = NGNode a (f (NGRose (Comp f f) a))
 * newtype Comp f g a = Comp (f (g a))
 */

sealed trait NGRose[F[_],A]

case class NGNode[F[_],A]
  (v : A, r : F[NGRose[({type l[a] = F[F[a]]})#l,A]]) extends NGRose[F,A]

