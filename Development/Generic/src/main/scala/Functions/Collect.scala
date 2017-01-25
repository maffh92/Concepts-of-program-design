package Functions

import scala.language.{higherKinds, postfixOps}
import scalaz.Alternative
import Base._
import Base.Ops._

/*
  Collect is a function A -> F[B] which under a type A it may
  collect values of type B on F[B].
  We do not constraint in the trait itself F to be Alternative, but
  delegate that to the actual implementation.
 */

trait Collect[F[_],B,A] {
  def collect_ : A => F[B]
}

object Collect {

  // This definition ensures that when a value of type B is found then
  // is injected with pure.
  def GRepCollect[F[_],B](implicit altf : Alternative[F]) = new
    GRep[({type C[X] = Collect[F,B,X]})#C,B] {
      def grep = new Collect[F,B,B] {
        def collect_ : B => F[B] = x => altf.pure(x)
      }
    }

  // We need to make Collect an instance of Generic[Collect[F,B]] given an Alternative
  // instance for F.
  implicit def CollectG[F[_],B] (implicit altf : Alternative[F]) = new Generic[({type C[X] = Collect[F,B,X]})#C] {
    def unit: Collect[F,B,Unit] = new Collect[F,B,Unit] {
      def collect_ = const(altf.empty[B]) _
    }
    def plus[A,C](a : Collect[F,B,A], c : Collect[F,B,C]) : Collect[F,B,Plus[A,C]] = new Collect[F,B,Plus[A,C]] {
      def collect_ =  p => p match {
        case Inl(l) => a.collect_(l)
        case Inr(r) => c.collect_(r)
      }
    }
    def product[A,C](a : Collect[F,B,A], c : Collect[F,B,C]) : Collect[F,B,Product[A,C]] = new Collect[F,B,Product[A,C]] {
      def collect_ = p => p match {
        case Product(l,r) => altf.plus(a.collect_(l),c.collect_(r))
      }
    }
    def char : Collect[F,B,Char] = new Collect[F,B,Char] {
      def collect_ = const(altf.empty[B]) _
    }
    def int : Collect[F,B,Int] = new Collect[F,B,Int] {
      def collect_ = const(altf.empty[B]) _
    }
    def string : Collect[F,B,String] = new Collect[F,B,String] {
      def collect_ = const(altf.empty[B])
    }
    def view[A,C](iso : Iso[C,A], a : () => Collect[F,B,A]) : Collect[F,B,C] = new Collect[F,B,C] {
        def collect_ = x => a().collect_(iso.from(x))
    }
  }

  // The method collect is the actual interface that we desire to use.
  def collect[F[_],B,A](a : A)(implicit altf : Alternative[F], grep : GRep[({type C[X] = Collect[F,B,X]})#C,A]) : F[B] =
  {
    grep.grep.collect_(a)
  }

}





