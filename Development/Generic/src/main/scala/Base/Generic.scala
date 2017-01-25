package Base

import scala.language.{higherKinds, postfixOps}

/*
Representation types.
*/

// Unit type
case object Unit

// Product type
case class Product[A, B](a: A, b: B)

// Coproduct type
sealed abstract class Plus[A, B]
case class Inl[A, B](a: A) extends Plus[A, B]
case class Inr[A, B](b: B) extends Plus[A, B]

// Iso[A,B] represents an isomorphism between types A and B.
trait Iso[A, B] {
  def from: A => B
  def to:   B => A
}

/*
  Generic: Base, kind of arguments *
*/
trait Generic[G[_]] {
  type Arity = Int
  type Name = String

  def unit: G[Unit]

  def plus[A, B](a: G[A], b: G[B]): G[Plus[A, B]]

  def product[A, B](a: G[A], b: G[B]): G[Product[A, B]]

  def constr[A](n: Name, ar: Arity, a: G[A]): G[A] = a

  def char: G[Char]

  def int: G[Int]

  def string: G[String]

  def view[A, B](iso: Iso[B, A], a: () => G[A]): G[B]
}

/*
  Generic: Kind of arguments * -> *
*/
trait Generic2[G[_, _]] {
  type Arity = Int
  type Name = String

  def unit: G[Unit, Unit]

  def plus[A1, A2, B1, B2](a: G[A1, A2], b: G[B1, B2]): G[Plus[A1, B1], Plus[A2, B2]]

  def product[A1, A2, B1, B2](a: G[A1, A2], b: G[B1, B2]): G[Product[A1, B1], Product[A2, B2]]

  def constr[A, Z](n: Name, ar: Arity, a: G[A, Z]): G[A, Z] = a

  def char: G[Char, Char]

  def int: G[Int, Int]

  def string: G[String, String]

  def view[A1, A2, B1, B2](iso1: Iso[A2, A1], iso2: Iso[B2, B1], a: () => G[A1, B1]): G[A2, B2]
}

trait Rep[A]{
  def rep[G[_]](implicit gg : Generic[G]) : G[A]
}

object Rep {
  implicit val grepUnit = {
    new Rep[Unit]{
      def rep[G[_]](implicit gg : Generic[G]) : G[Unit] = gg.unit
    }
  }
}

/*
  Generic representation.
*/

trait GRep[G[_], A] {
  def grep: G[A]
}

object GRep {

  def apply[G[_], A](implicit e: GRep[G, A]): GRep[G, A] = e

  implicit def GUnit[G[_]](implicit gg: Generic[G]): GRep[G, Unit] = new GRep[G, Unit] {
    def grep: G[Unit] = gg.unit
  }

  implicit def GInt[G[_]](implicit gg: Generic[G]) = new GRep[G, Int] {
    def grep: G[Int] = gg.int
  }

  implicit def GChar[G[_]](implicit gg: Generic[G]) = {
    new GRep[G, Char] {
      def grep: G[Char] = gg.char
    }
  }

  implicit def GString[G[_]](implicit gg: Generic[G]): GRep[G, String] = new GRep[G, String] {
    def grep: G[String] = gg.string
  }

  implicit def GPlus[A, B, G[_]](implicit gg: Generic[G], a: GRep[G, A], b: GRep[G, B]) = {
    new GRep[G, Plus[A, B]] {
      def grep: G[Plus[A, B]] = gg.plus[A, B](a.grep, b.grep)
    }
  }

  implicit def GProduct[A, B, G[_]](implicit gg: Generic[G], a: GRep[G, A], b: GRep[G, B]) = {
    new GRep[G, Product[A, B]] {
      def grep: G[Product[A, B]] = gg.product[A, B](a.grep, b.grep)
    }
  }
}

/*
  FREP
*/

trait FRep[G[_],F[_]]{
  def frep[A](g1 : G[A]) : G[F[A]]
}

trait FRep2[G[_,_],F[_]]{
  def frep2[A,B](g1 : G[A,B]) : G[F[A],F[B]]
}

