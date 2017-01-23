package Functions

import Base.Generic._
import Base.Ops._

import scala.language.{higherKinds, postfixOps}

object EverywhereObject {

  abstract class Everywhere[A,B] {
    def everywhere_ : (A => A) => B => B
  }

  object  Everywhere {
    implicit def mkEverywhere[A] : EverywhereC[A] = new EverywhereC[A]
  }

  class EverywhereC[A] extends Generic[({type C[B] = Everywhere[A,B]})#C] {
    def unit : Everywhere[A, Unit] = new Everywhere[A, Unit] {
      def everywhere_ = const(id(_) ) _
    }

    def plus[D,E](d : Everywhere[A,D], e : Everywhere[A,E]) : Everywhere[A,Plus[D,E]] = {
      new Everywhere[A,Plus[D,E]] {
        def everywhere_ = (f : A => A) => (p : Plus[D,E]) => p match {
          case Inl(l) => Inl(d.everywhere_(f)(l))
          case Inr(r) => Inr(e.everywhere_(f)(r))
        }
      }
    }

    def product[D,E](d : Everywhere[A,D], e : Everywhere[A,E]) : Everywhere[A,Product[D,E]] = {
      new Everywhere[A,Product[D,E]] {
        def everywhere_ = (f : A => A) => (p : Product[D,E]) => p match {
          case Product(l,r) => Product(d.everywhere_(f)(l),e.everywhere_(f)(r))
        }
      }
    }

    def char : Everywhere[A,Char] = new Everywhere[A,Char] {
      def everywhere_ = const(id(_)) _
    }

    def int : Everywhere[A,Int] = new Everywhere[A,Int] {
      def everywhere_ = const(id(_)) _
    }

    def view[D,E](iso : Iso[E,D], a : () => Everywhere[A,D]) : Everywhere[A,E] = {
      new Everywhere[A,E] {
        def everywhere_ =  (f : A => A) => (x : E) => iso.to(a().everywhere_(f)(iso.from(x)))
      }
    }
  }

  def everywhere[A,B](f : A => A, b : B)(implicit grep : GRep[({type C[B] = Everywhere[A,B]})#C,B]) : B =
  {
    grep.grep.everywhere_(f)(b)
  }
}

