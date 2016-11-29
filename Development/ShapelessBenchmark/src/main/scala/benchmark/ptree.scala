package benchmark

/*
 * data Perfect a = Zero a | Succ (Perfect (Fork a))
 * data Fork a = Fork a a
 */

sealed trait Perfect[A]

case class Zero[A](v : A) extends Perfect[A]
case class Succ[A](v : A) extends Perfect[TFork[A]]

sealed trait TFork[A]

case class Fork[A](l : A, r : A) extends TFork[A]
