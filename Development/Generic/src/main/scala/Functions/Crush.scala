package Functions

import Base._
import Base.Ops._

import scala.language.{higherKinds, postfixOps}

/*
This file contains the implementation of the Crush function. You can see Crush as a general function of a fold.
*/

sealed trait Assoc
case object AssocLeft extends Assoc
case object AssocRight extends Assoc

/*
  Crush is a fold like function that given a container
  folds its elements with the suplied function.
 */
trait Crush[B,A] {
  def selCrush(asc: Assoc)(a: A)(b: B) :  B
}

object Crush {
  implicit def CrushC[B] = new Generic[({type AB[A] = Crush[B, A]})#AB] {
    def idCrush[A]: Crush[B, A] = new Crush[B, A] {
      override def selCrush(asc: Assoc)(a: A)(b: B) = id(b)
    }

    def unit = idCrush

    def char = idCrush

    def int = idCrush

    def string = idCrush

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
  }
  // The actually crush function.
  def crush[B, A, F[_]](asc: Assoc)(f: A => B => B)(z: B)(x: F[A])(implicit rep: FRep[({type AB[A] = Crush[B, A]})#AB, F]): B = {
    val fCrush = new Crush[B, A] {
      def selCrush(asc: Assoc)(a: A)(b: B) = f(a)(b)
    }
    rep.frep(fCrush).selCrush(asc)(x)(z)
  }

  // Crush right associative shorthand
  def crushr[B, A, F[_]](f: A => B => B)(z: B)(x: F[A])(implicit rep: FRep[({type AB[A] = Crush[B, A]})#AB, F]): B = {
    crush(AssocRight)(f)(z)(x)
  }

  // Crush with left associativity
  def crushl[B, A, F[_]](f: A => B => B)(z: B)(x: F[A])(implicit rep: FRep[({type AB[A] = Crush[B, A]})#AB, F]): B = {
    crush(AssocLeft)(f)(z)(x)
  }

  // Crush with Monoid.
  def crushrM[B, F[_]](x: F[B])(implicit mon: Monoid[B], rep: FRep[({type AB[X] = Crush[B, X]})#AB, F]): B = {
      crushr((a : B) => (b : B) => mon.mappend(a,b))(mon.mempty)(x)
    }

  def crushlM[B, F[_]](x: F[B])(implicit mon: Monoid[B], rep: FRep[({type AB[X] = Crush[B, X]})#AB, F]): B = {
    crushl((a : B) => (b : B) => mon.mappend(a,b))(mon.mempty)(x)
  }
}



