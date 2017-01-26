package Functions

import Base._
import Base.Ops._

import scala.language.{higherKinds, postfixOps}

/*
 * Everywhere represents the "map" of a function A -> A
 * inside some other type B.
 */
trait Everywhere[A,B] {
  def everywhere_ : (A => A) => B => B
}

object  Everywhere {

  implicit def GRepEverywhere[A] = new GRep[({type C[X] = Everywhere[A,X]})#C,A] {
    def grep = new Everywhere[A,A] {
      def everywhere_ : (A => A) => A => A = f => a => f(a)
    }
  }

  implicit def EverywhereC[A] = new Generic[({type C[X] = Everywhere[A,X]})#C] {
    def unit : Everywhere[A, Unit] = new Everywhere[A, Unit] {
      def everywhere_ = const(id(_) ) _
    }

    def plus[D,E](d : Everywhere[A,D], e : Everywhere[A,E]) : Everywhere[A,Plus[D,E]] = new Everywhere[A,Plus[D,E]] {
      def everywhere_ = (f : A => A) => (p : Plus[D,E]) => p match {
        case Inl(l) => Inl(d.everywhere_(f)(l))
        case Inr(r) => Inr(e.everywhere_(f)(r))
      }
    }

    def product[D,E](d : Everywhere[A,D], e : Everywhere[A,E]) : Everywhere[A,Product[D,E]] = new Everywhere[A,Product[D,E]] {
      def everywhere_ = (f : A => A) => (p : Product[D,E]) => p match {
        case Product(l,r) => Product(d.everywhere_(f)(l),e.everywhere_(f)(r))
      }
    }

    def char : Everywhere[A,Char] = new Everywhere[A,Char] {
      def everywhere_ = const(id(_)) _
    }

    def int : Everywhere[A,Int] = new Everywhere[A,Int] {
      def everywhere_ = const(id(_)) _
    }

    def string : Everywhere[A,String] = new Everywhere[A,String] {
      def everywhere_ = const(id(_)) _
    }

    def view[D,E](iso : Iso[E,D], a : () => Everywhere[A,D]) : Everywhere[A,E] = {
      new Everywhere[A,E] {
        def everywhere_ =  (f : A => A) => (x : E) => iso.to(a().everywhere_(f)(iso.from(x)))
      }
    }
  }

  def everywhere[A,B](f : A => A, b : B)(implicit grep : GRep[({type C[X] = Everywhere[A,X]})#C,B]) : B = {
    grep.grep.everywhere_(f)(b)
  }
}

