package types

/*
 * data NGRose f a
 *   = NGNode a (f (NGRose (Comp f f) a))
 * newtype Comp f g a = Comp (f (g a))
 */

case class NGRose[F[_],A]
  (v : A, r : F[NGRose[({type l[a] = F[F[a]]})#l,A]])

