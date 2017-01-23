package Functions

import Base.Generic._
import Base.Ops._

trait Map[A,B]{
  def selMap(a : A)  : B
}

object Map {
  implicit object MapC extends Generic2[Map] {

    def idMap[A]: Map[A, A] = new Map[A, A] {
      def selMap(a: A) = id(a)
    }

    def unit = idMap

    def char = idMap

    def int = idMap

    //In the product we map the function to both variables.
    def product[A1, A2, B1, B2](ra: Map[A1, A2], rb: Map[B1, B2]) = new Map[Product[A1, B1], Product[A2, B2]] {
      def selMap(product: Product[A1, B1]) = product match {
        case Product(a, b) => Product(ra.selMap(a), rb.selMap(b))
      }
    }

    //In the plus function we either map the function to the left or right variable
    def plus[A1, A2, B1, B2](ra: Map[A1, A2], rb: Map[B1, B2]) = new Map[Plus[A1, B1], Plus[A2, B2]] {
      def selMap(plus: Plus[A1, B1]) = plus match {
        case Inl(x) => Inl(ra.selMap(x))
        case Inr(x) => Inr(rb.selMap(x))
      }
    }

    def view[A1, A2, B1, B2](iso1: Iso[A2, A1], iso2: Iso[B2, B1], ra: () => Map[A1, B1]): Map[A2, B2] = new Map[A2, B2] {
      def selMap(a2: A2): B2 = iso2.to(ra().selMap(iso1.from(a2)))
    }
  }
  /*
    The map function maps over each Data type, which has an implicit implementation of the FRep2.
    first add the f to the new instance of the map
    then map over the functor variable.
  */
  def map[A,B,F[_]](f : A => B)(functor : F[A])(implicit rep : FRep2[Map, F]) : F[B] = {
    val fMap = new Map[A,B] {
      def selMap(a : A) = f(a)
    }
    return(rep frep2(fMap) selMap(functor))
  }
}
