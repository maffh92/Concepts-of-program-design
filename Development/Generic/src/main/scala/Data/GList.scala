package Data
import scala.language.higherKinds
import scala.language.postfixOps
import Base.GenericObject._
import Functions.MapObject._
import  Functions.CrushObject._


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



  implicit def RepList[G[_]](implicit g : Generic[G]) : FRep[G,List] = {
    new FRep[G,List] {
      def frep[A](g1 : G[A]) : G[List[A]] = {
        frepList(g1)
      }
    }
  }

  implicit def RepList2[B](implicit g : Generic[({type AB[A] = Functions.CrushObject.Crush[B,A]})#AB]) : Base.GenericObject.FRep[({type AB[A] = Functions.CrushObject.Crush[B,A]})#AB,List] = {
    new FRep[({type AB[X] = Crush[B,X]})#AB,List]{
      override def frep[A](g1: Crush[B, A]): Crush[B, List[A]] = frepList2(g1)
    }
  }



  def frepList2[A,B](g : Crush[B, A])(implicit gg : Generic[({type AB[A] = Crush[B,A]})#AB]): Crush[B, List[A]] = {
    gg.view(listIso[A],() => gg.plus(gg.unit,gg.product(g,frepList2[A,B](g))))
  }

  implicit val Rep2List = new Base.GenericObject.FRep2[Functions.MapObject.Map,List]{
    def frep2[A,B](g1: Map[A,B]) : Map[List[A],List[B]] = {
      frep2List(g1)
    }
  }

  //  Need to write out the full type for implicits in a different file. Need to find out why
  //  implicit def Rep2List[G[_,_]](implicit g : Generic2[G]) : FRep2[G,List] = new FRep2[G,List] {
  //    def frep2[A,B](g1 : G[A,B]) : G[List[A], List[B]] = {
  //      frep2List(g1)
  //    }
  //  }
//  implicit def RepList2[G[_,_],B](implicit g : Generic[({type AB[A] = G[B,A]})#AB]) : FRep[({type AB[A] = G[B,A]})#AB,List] = {
//    new FRep[({type AB[X] = G[B,X]})#AB,List]{
//      override def frep[A](g1: G[B, A]): G[B, List[A]] = frepList2(g1)
//    }
//  }
//  implicit def RepList2[G[_,_],B](implicit g : Generic[({type AB[A] = G[B,A]})#AB]) : FRep[({type AB[A] = G[B,A]})#AB,List] = {
//    new FRep[({type AB[X] = G[B,X]})#AB,List]{
//      override def frep[A](g1: G[B, A]): G[B, List[A]] = frepList2(g1)
//    }
//  }
}
