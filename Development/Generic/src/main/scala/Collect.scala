import scala.language.higherKinds
import scala.language.postfixOps
import GenericObject._

import scalaz.{Alternative, ApplicativePlus}

object CollectObject {

  abstract class Collect[F[_],B,A] {
      def collect_ : A => F[B]
   }

  object Collect {
    implicit def mkCollectC[F[_],B](implicit altf : Alternative[F]) : CollectC[F,B] = new CollectC[F,B]
  }

  class CollectC[F[_],B] (implicit altf : Alternative[F])
     extends Generic[({type C[A] = Collect[F,B,A]})#C] {

     def unit: Collect[F,B,Unit] =
       {
         new Collect[F,B,Unit] {def collect_ = const(altf.empty[B]) _ }
       }
     def plus[A,C](a : Collect[F,B,A], c : Collect[F,B,C]) : Collect[F,B,Plus[A,C]] = {
       new Collect[F,B,Plus[A,C]]
        {
           def collect_ =  p => p match {
             case Inl(l) => a.collect_(l)
             case Inr(r) => c.collect_(r)
           }
        }
     }
    def product[A,C](a : Collect[F,B,A], c : Collect[F,B,C]) : Collect[F,B,Product[A,C]] = {
      new Collect[F,B,Product[A,C]] {
        def collect_ = p => p match {
          case Product(l,r) => altf.plus(a.collect_(l),c.collect_(r))
        }
      }
    }
    def char : Collect[F,B,Char] = {
      new Collect[F,B,Char] { def collect_ = const(altf.empty[B]) _ }
    }
    def int : Collect[F,B,Int] = {
      new Collect[F,B,Int] { def collect_ = const(altf.empty[B]) _ }
    }
    def view[A,C](iso : Iso[C,A], a : () => Collect[F,B,A]) : Collect[F,B,C] = {
      new Collect[F,B,C]
      {
        def collect_ = x => a().collect_(iso.from(x))
      }
    }
   }

  def collect[F[_],B,A](a : A)(implicit altf : Alternative[F], grep : GRep[({type C[A] = Collect[F,B,A]})#C,A]) =
    {
      grep.grep.collect_(a)
    }

}

