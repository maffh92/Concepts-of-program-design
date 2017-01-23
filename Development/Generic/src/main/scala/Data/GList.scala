package Data
import scala.language.higherKinds
import scala.language.postfixOps
import Base.Generic._
import Base.Instances._
import Functions._
import Functions.Crush._
import Functions.Map._

object GList {
  //General representation of a list. A list is either a Cons or a Cons with a tail
  type ListS[A] = Plus[Unit,Product[A,List[A]]]

  /*
  The functions FromList and ToList are just to create a isomorphic function in
  order to go from the general representation and the other way around.
  listIso is the isomorphic function
   */

  def fromList[A](l : List[A]) : ListS[A] =
    l match {
      case Nil 	   => Inl(Unit)
      case (x :: xs) => Inr(Product(x,xs))
    }

  def toList[A](r : ListS[A]) : List[A] =
    r match {
      case Inl(_) => Nil
      case Inr(Product(x,xs)) => x :: xs
    }

  def listIso[A]  = new Iso[List[A],ListS[A]] {
    def from = fromList
    def to = toList
  }

  /*
    frepList is used as a helper function for the general dispatcher. It uses the Generic class that just takes 1 parameter.
   */
  def frepList[A,G[_]](g : G[A])(implicit gg : Generic[G]): G[List[A]] = {
    gg.view(listIso[A],() => gg.plus(gg.unit,gg.product(g,frepList[A,G](g))))
  }
  implicit def RepList[G[_]](implicit g : Generic[G]) : FRep[G,List] = {
    new FRep[G,List] {
      def frep[A](g1 : G[A]) : G[List[A]] = frepList(g1)
    }
  }

  /* frepListCurried is creates an instance for the general dispatcher FRep,
    which can be used by the functions extended by the Generic class that takes 1 parameter.
   The difference between frepList is that this function uses a lambda type, such that it can obtain the parameter B when the frep function is called.
   */
  def frepListCrush[A,B](g : Crush[B, A])(implicit gg : Generic[({type AB[A] = Crush[B,A]})#AB]): Crush[B, List[A]] = {
    gg.view(listIso[A],() => gg.plus(gg.unit,gg.product(g,frepListCrush[A,B](g))))
  }
  implicit def frepListCurried[B](implicit g : Generic[({type AB[A] = Functions.Crush[B,A]})#AB]) : Base.Generic.FRep[({type AB[A] = Functions.Crush[B,A]})#AB,List] = {
    new FRep[({type AB[X] = Crush[B,X]})#AB,List]{
      override def frep[A](g1: Crush[B, A]): Crush[B, List[A]] = frepListCrush(g1)
    }
  }

    /*
      frep2List is used as a helper function for the general dispatcher. It uses the Generic2 class that just takes 2 parameters.
      It is explicit uses for the Map function. Unfortunately, we could not solve to it using a general parameter.
     */
  def frep2List[A,B,G[_,_]](g : G[A,B])(implicit gg : Generic2[G]): G[List[A],List[B]] = {
    gg.view(listIso[A],listIso[B],() => gg.plus(gg.unit,gg.product(g,frep2List[A,B,G](g))))
  }

  implicit val Rep2List = new Base.Generic.FRep2[Functions.Map,List]{
    def frep2[A,B](g1: Map[A,B]) : Map[List[A],List[B]] = {
      frep2List(g1)
    }
  }

/*
* The below code defines a Rep dispatcher, but our current functions do not use this Rep dispatcher.
 * The difference between Frep and Rep is that for Rep we have to define for every type a dispatcher(i.e int,char, etc)
* */

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
}
