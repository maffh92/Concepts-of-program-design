package Functions

import Base.Generic._
import Base.Ops._

import scala.language.{higherKinds, postfixOps}

/*
This file contains the implementation of the Crush function. You can see Crush as a general function of a fold.
*/

sealed trait Assoc
case object AssocLeft extends Assoc
case object AssocRight extends Assoc

trait Crush[B,A] {
  def selCrush(asc: Assoc)(a: A)(b: B) :  B
}

  //The first time the project gets compiled it will give an error of ambiguous errors.
  // In order to solve this error you have to comment the mkCrush function and build the project.
  // Next uncomment the file and build the project again.
  // The second time you build the project should not give any errors.
  //implicit def mkCrush[B] : crushC[B] = new crushC[B]

object Crush {

  class CrushC[B] extends Generic[({type AB[A] = Crush[B, A]})#AB] {
    def idCrush[A]: Crush[B, A] = new Crush[B, A] {
      override def selCrush(asc: Assoc)(a: A)(b: B) = id(b)
    }

    def unit = idCrush

    def char = idCrush

    def int = idCrush

    def plus[X, Y](ra: Crush[B, X], rb: Crush[B, Y]) = new Crush[B, Plus[X, Y]] {
      def selCrush(asc: Assoc)(plus: Plus[X, Y])(d: B): B = plus match {
        case Inl(a) => ra.selCrush(asc)(a)(d)
        case Inr(b) => rb.selCrush(asc)(b)(d)
      }
    }

    def product[X, Y](ra: Crush[B, X], rb: Crush[B, Y]) = new Crush[B, Product[X, Y]] {
      def selCrush(asc: Assoc)(product: Product[X, Y])(d: B): B = product match {
        case Product(a, b) => asc match {
          case AssocLeft => rb.selCrush(asc)(b)(ra.selCrush(asc)(a)(d))
          case AssocRight => ra.selCrush(asc)(a)(rb.selCrush(asc)(b)(d))
        }
      }
    }

    def view[X, Y](iso1: Iso[Y, X], ra: () => Crush[B, X]): Crush[B, Y] = new Crush[B, Y] {
      def selCrush(asc: Assoc)(a: Y)(b: B): B = ra().selCrush(asc)(iso1.from(a))(b)
    }

    // The actually crush functions:
    def crush[B, A, F[_]](asc: Assoc)(f: A => B => B)(z: B)(x: F[A])(implicit rep: FRep[({type AB[A] = Crush[B, A]})#AB, F]): B = {
      val fCrush = new Crush[B, A] {
        def selCrush(asc: Assoc)(a: A)(b: B) = f(a)(b)
      }
      rep.frep(fCrush).selCrush(asc)(x)(z)
    }

    def crushr[B, A, F[_]](f: A => B => B)(z: B)(x: F[A])(implicit rep: FRep[({type AB[A] = Crush[B, A]})#AB, F]): B = {
      crush(AssocRight)(f)(z)(x)
    }

    def crushl[B, A, F[_]](f: A => B => B)(z: B)(x: F[A])(implicit rep: FRep[({type AB[A] = Crush[B, A]})#AB, F]): B = {
      crush(AssocLeft)(f)(z)(x)
    }

    //The sum function takes the product of some data generic data type, which has a general dispatcher defined by the FRep class.
    def sum[B, F[_]](x: F[B])(implicit number: Number[B], rep: FRep[({type AB[X] = Crush[B, X]})#AB, F]): B = {
      crushr(number.plus)(number.zero)(x)
    }


    def product[B, F[_]](x: F[B])(implicit number: Number[B], rep: FRep[({type AB[X] = Crush[B, X]})#AB, F]): B = {
      crushr(number.multiple)(number.one)(x)
    }


    //The number trait is used in a similar way as the Num a in Haskell.
    trait Number[A] {
      // an identity element
      def zero: A

      def one: A

      // an associative operation
      def plus(x: A)(y: A): A

      def multiple(x: A)(y: A): A
    }

    implicit val IntMonoid = new Number[Int] {
      def zero = 0

      def one = 1

      def plus(x: Int)(y: Int) = x + y

      def multiple(x: Int)(y: Int) = x * y
    }

  }
}



