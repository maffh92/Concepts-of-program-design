package Functions

import Base.GenericObject._

import scala.language.{higherKinds, postfixOps}
/**
  * This file is still under construction, because the representation for the Lists and the crush does not compile.
  */
object CrushObject {

  class Assoc
  case object AssocLeft extends Assoc
  case object AssocRight extends Assoc

  trait Crush[B,A] {
    def selCrush: Assoc => A => B => B
  }

  def rsumCrush[A,B,D](ra : Crush[D,A])(rb : Crush[D,B])(asc : Assoc)(plus : Plus[A,B])(d : D) : D = {
    plus match{
      case Inl(a) => ra.selCrush(asc)(a)(d)
      case Inr(b) => rb.selCrush(asc)(b)(d)
    }
  }

  def rprodCrush[A,B,D](ra : Crush[D,A])(rb : Crush[D,B])(asc : Assoc)(product : Product[A,B])(d : D) : D = {
    product match{
      case Product(a,b) => {
        asc match{
          case AssocLeft => rb.selCrush(asc)(b)(ra.selCrush(asc)(a)(d))
          case AssocRight => ra.selCrush(asc)(a)(rb.selCrush(asc)(b)(d))
        }
      }
    }

  }


  def rtypeCrush[A,B,D](iso1 : Iso[B,A])(ra : Crush[D,A])(asc : Assoc)(b : B)(d : D) : D  = {
    ra.selCrush(asc)(iso1.from(b))(d)
  }

  implicit def mkCrush[B] : crushC[B] = new crushC[B]

  class crushC[B]extends Generic[({type AB[A] = Crush[B,A]})#AB]{


    def idCrush[A] =
    {
      new Crush[B,A]{
        override def selCrush = _ => _ => id
      }
    }

    def unit = idCrush
    def char = idCrush
    def int  = idCrush

    //

    def plus[X,Y](ra : Crush[B,X], rb : Crush[B,Y]) =
    {
      new Crush[B,Plus[X,Y]]{
        def selCrush = rsumCrush[X,Y,B](ra)(rb)
      }
    }

    def product[X,Y](ra : Crush[B,X], rb : Crush[B,Y]) =
    {
      new Crush[B,Product[X,Y]]{
        def selCrush = rprodCrush[X,Y,B](ra)(rb)
      }
    }


    def view[X,Y](ra : Crush[B,X], rb : Crush[B,Y]) =
    {
      new Crush[B,Product[X,Y]]{
        def selCrush = rprodCrush[X,Y,B](ra)(rb)
      }
    }
    // def product[A,B](a : G[A], b : G[B]) : G[Product[A,B]]
    def view[X,Y](iso1 : Iso[Y,X], a: () => Crush[B,X]) : Crush[B,Y] = {
      new Crush[B,Y]{
        def selCrush = rtypeCrush[X,Y,B](iso1)(a())
      }
    }

  }


// The actually crush functions:
  def crush[B,A,F[_]](asc : Assoc)(f : A => B => B)(z : B)(x : F[A])(implicit rep : FRep[({type AB[A] = Crush[B,A]})#AB,F]): B = {
    val fCrush = new Crush[B,A]{
      override def selCrush= _ => f
    }
    rep.frep(fCrush).selCrush(asc)(x)(z)
  }

  def crushr[B,A,F[_]](f : A => B => B)(z : B)(x : F[A])(implicit rep : FRep[({type AB[A] = Crush[B,A]})#AB,F]): B = {
    crush(AssocRight)(f)(z)(x)
  }

  def crushl[B,A,F[_]](f : A => B => B)(z : B)(x : F[A])(implicit rep : FRep[({type AB[A] = Crush[B,A]})#AB,F]): B = {
    crush(AssocLeft)(f)(z)(x)
  }


  def sum[B,F[_]](x : F[B])(implicit number : Number[B], rep : FRep[({type AB[X] = Crush[B,X]})#AB,F]) : B = {
    crushr(number.plus)(number.zero)(x)
  }

  def product[B,F[_]](x : F[B])(implicit number : Number[B], rep : FRep[({type AB[X] = Crush[B,X]})#AB,F]) : B = {
    crushr(number.multiple)(number.one)(x)
  }

  trait Number[A] {
    // an identity element
    def zero : A
    def one : A
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



