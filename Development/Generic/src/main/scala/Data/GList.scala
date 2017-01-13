package Data
import scala.language.higherKinds
import scala.language.postfixOps
import Base.GenericObject._
import Functions.MapObject._
/**
  * Created by maffh on 8-1-17.
  */
object GList {
  type ListS[A] = Plus[Unit,Product[A,List[A]]]

  def fromList[A] : (List[A] => ListS[A]) = l =>
    l match {
      case Nil 	   => Inl(Unit)
      case (x :: xs) => Inr(Product(x,xs))
    }

  def toList[A] : (ListS[A] => List[A]) = r =>
    r match {
      case Inl(_) => Nil
      case Inr(Product(x,xs)) => x :: xs
    }

  def listIso[A]  = new Iso[List[A],ListS[A]] {
    def from = fromList
    def to = toList
  }

  def frepList[A,G[_]](g : G[A])(implicit gg : Generic[G]): G[List[A]] = {
    gg.view(listIso[A],() => gg.plus(gg.unit,gg.product(g,frepList[A,G](g))))
  }


  def frep2List[A,B,G[_,_]](g : G[A,B])(implicit gg : Generic2[G]): G[List[A],List[B]] = {
    gg.view(listIso[A],listIso[B],() => gg.plus(gg.unit,gg.product(g,frep2List[A,B,G](g))))
  }



  trait Rep[A]{
    def rep[G[_]](implicit gg : Generic[G]) : G[A]
  }

  implicit object RepUnit extends Rep[Unit]{
    def rep[G[_]](implicit gg : Generic[G]) : G[Unit] = gg unit
  }

  implicit object RepChar extends Rep[Char]{
    def rep[G[_]](implicit gg : Generic[G]) : G[Char] = gg char
  }


  implicit object RepInt extends Rep[Int]{
    def rep[G[_]](implicit gg : Generic[G]) : G[Int] = gg int
  }

  implicit class RepPlus[A,B] (a : Rep[A])(implicit b : Rep[B]) extends Rep[Plus[A,B]]{
    def rep[G[_]](implicit gg : Generic[G]) : G[Plus[A,B]] = {
      gg plus[A,B](a.rep,b.rep)
    }
  }

  class RepProduct[A,B] (implicit a : Rep[A],b : Rep[B]) extends Rep[Product[A,B]]{
    def rep[G[_]](implicit gg : Generic[G]) : G[Product[A,B]] = {
      gg product[A,B](a.rep,b.rep)
    }
  }


  class RepList[A] (implicit a : Rep[A]) extends Rep[List[A]]{
    def rep[G[_]](implicit gg : Generic[G]) : G[List[A]] = {
      frepList(a.rep)
    }
  }

  trait FRep[G[_],F[_]]{
    def frep[A](g1 : G[A]) : G[F[A]]
  }

  trait FRep2[G[_,_],F[_]]{
    def frep2[A,B](g1 : G[A,B]) : G[F[A],F[B]]
  }

  implicit object Rep2List extends Base.GenericObject.FRep2[Functions.MapObject.Map,List]{
    def frep2[A,B](g1: Map[A,B]) : Map[List[A],List[B]] = {
      frep2List(g1)
    }
  }

}
