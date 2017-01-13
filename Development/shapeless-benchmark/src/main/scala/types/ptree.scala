package types

/*
 * data Perfect a = Zero a | Succ (Perfect (Fork a))
 * data Fork a = Fork a a
 *
 * data Perfect a = Zero a | Succ (Perfect (a,a))
 */

sealed trait Perfect[A]

case class Zero[A](v : A) extends Perfect[A]
case class Succ[A](v : Perfect[(A,A)]) extends Perfect[A]