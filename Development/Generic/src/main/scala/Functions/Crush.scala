package Functions

import Base.GenericObject._

import scala.language.{higherKinds, postfixOps}
/**
  * Created by maffh on 8-1-17.
  */
object CrushObject {

  def frepList[A,G[_]](g : G[A])(implicit gg : Generic[G]): G[List[A]] = {
    gg.view(listIso[A],() => gg.plus(gg.unit,gg.product(g,frepList[A,G](g))))
  }


  def frep2List[A,B,G[_,_]](g : G[A,B])(implicit gg : Generic2[G]): G[List[A],List[B]] = {
    gg.view(listIso[A],listIso[B],() => gg.plus(gg.unit,gg.product(g,frep2List[A,B,G](g))))
  }



  class Assoc
  case object AssocLeft extends Assoc
  case object AssocRight extends Assoc

  trait Crush[B,A]{
    def selCrush : Assoc => A => B => B
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

  object  crushC {
    implicit def mkCrush[B] : crushC[B] = new crushC[B]
  }

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


  /*
  Representations

  */


  implicit def Rep2List[G[_,_]](implicit g : Generic2[G]) : FRep2[G,List] = new FRep2[G,List] {
    def frep2[A,B](g1 : G[A,B]) : G[List[A], List[B]] = {
      frep2List(g1)
    }
  }



  implicit def RepList[G[_]](implicit g : Generic[G]) : FRep[G,List] = {
    new FRep[G,List] {
      def frep[A](g1 : G[A]) : G[List[A]] = {
        frepList(g1)
      }
    }
  }


//  implicit def RepList2[G[_]](implicit g : Generic[({type AB[A] = Crush[Int,A]})#AB]) : FRep[({type AB[A] = Crush[Int,A]})#AB,List] = {
//    new FRep[({type AB[X] = Crush[Int,X]})#AB,List] {
//      def frep[Z](g1 : ({type ZA[A] = Crush[Int,A]})#ZA[Z]) :  ({type ZB[A] = Crush[List[Int],A]})#ZB[Z]  = {
////        frepList(g1)
//      }
//    }
//  }


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

  // def plusFunction[A](x : String)(y : String) = x + y

  def sum[X,B,F[_]](x : F[B])(implicit number : Number[B], rep : FRep[({type AB[X] = Crush[B,X]})#AB,F]) : scala.Unit = {
    val som = crushr(number.plus)(number.empty)(x)
  }


  // def hoi[F[_],B] (implicit rep : FRep[({type AB[X] = Crush[B,X]})#AB,F])  = rep
  // val hoi1 = hoi

  trait Number[A] {
    // an identity element
    def empty: A
    // an associative operation
    def plus(x: A)(y: A): A
    def multiple(x: A)(y: A): A
  }

  implicit val IntMonoid = new Number[Int] {
    def empty = 0
    def plus(x: Int)(y: Int) = x + y
    def multiple(x: Int)(y: Int) = x * y
  }

}



