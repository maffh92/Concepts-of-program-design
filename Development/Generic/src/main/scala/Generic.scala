import scala.language.higherKinds
import scala.language.postfixOps

object GenericObject {

  /*
	Representation types
*/

  sealed class Unit

  object Unit extends Unit

  case class Product[A, B](a: A, b: B)

  class Plus[A, B]

  case class Inl[A, B](a: A) extends Plus[A, B]

  case class Inr[A, B](b: B) extends Plus[A, B]

  trait Iso[A, B] {
    def from: A => B

    def to: B => A
  }

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

  def listIso[A] = new Iso[List[A], Plus[Unit, Product[A, List[A]]]] {
    def from = fromList

    def to = toList
  }

  def rList[A, G[_]](g: G[A])(implicit gg: Generic[G]): G[List[A]] = {
    gg.view(listIso[A], () => gg.plus(gg.unit, gg.product(g, rList[A, G](g))))
  }

  // Not sure if correct
  abstract class GenericList[G[_]](implicit gg: Generic[G]) {
    def list[A]: G[A] => G[List[A]] = {
      x => rList(x)
    }
  }

  type Arity = Int
  type Name = String


  /*
	Generic: Base
*/

  trait Generic[G[_]] {
    def unit: G[Unit]

    def plus[A, B](a: G[A], b: G[B]): G[Plus[A, B]]

    def product[A, B](a: G[A], b: G[B]): G[Product[A, B]]

    def constr[A](n: Name, ar: Arity, a: G[A]): G[A] = a

    def char: G[Char]

    def int: G[Int]

    def view[A, B](iso: Iso[B, A], a: () => G[A]): G[B]
  }


  trait Generic2[G[_, _]] {
    def unit: G[Unit, Unit]

    def plus[A1, A2, B1, B2](a: G[A1, A2], b: G[B1, B2]): G[Plus[A1, B1], Plus[A2, B2]]

    def product[A1, A2, B1, B2](a: G[A1, A2], b: G[B1, B2]): G[Product[A1, B1], Product[A2, B2]]

    def constr[A, Z](n: Name, ar: Arity, a: G[A, Z]): G[A, Z] = a

    def char: G[Char, Char]

    def int: G[Int, Int]

    def view[A1, A2, B1, B2](iso1: Iso[A2, A1], iso2: Iso[B2, B1], a: () => G[A1, B1]): G[A2, B2]
  }


  trait GRep[G[_], A] {
    def grep: G[A]
  }

  class GUnit[G[_]](implicit gg: Generic[G]) extends GRep[G, Unit] {
    def grep: G[Unit] = gg.unit
  }

  class GInt[G[_]](implicit gg: Generic[G]) extends GRep[G, Int] {
    def grep: G[Int] = gg.int
  }

  class GChar[G[_]](implicit gg: Generic[G]) extends GRep[G, Char] {
    def grep: G[Char] = gg.char
  }

  class GPlus[A, B, G[_]](implicit gg: Generic[G], a: GRep[G, A], b: GRep[G, B]) extends GRep[G, Plus[A, B]] {
    def grep: G[Plus[A, B]] = gg.plus[A, B](a.grep, b.grep)
  }


  class Gproduct[A, B, G[_]](implicit gg: Generic[G], a: GRep[G, A], b: GRep[G, B]) extends GRep[G, Product[A, B]] {
    def grep: G[Product[A, B]] = gg.product[A, B](a.grep, b.grep)
  }

  class GList[A, G[_]](implicit glg: GenericList[G], a: GRep[G, A]) extends GRep[G, List[A]] {
    def grep: G[List[A]] = glg.list[A](a.grep)
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

  def const[A,B](a : A)(b : B) : A = a
  def id[A](a : A) : A = a


}


