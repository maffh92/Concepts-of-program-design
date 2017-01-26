package Data
import scala.language.higherKinds
import scala.language.postfixOps
import Base._
import Functions._
import Functions.Crush._
import Functions.Map._

object List {
  /*
    Generic representation of a list. A list is either a Cons or a Cons with a tail
  */
  type ListRep[A] = Plus[Unit, Product[A, List[A]]]

  /*
  The functions FromList and ToList are just to create a isomorphic function in
  order to go from the general representation and the other way around.
  listIso is the isomorphic function
   */

  implicit def listIso[A] = new Iso[List[A], ListRep[A]] {
    def fromList[A](l: List[A]): ListRep[A] =
      l match {
        case Nil => Inl(Unit)
        case (x :: xs) => Inr(Product(x, xs))
      }

    def toList[A](r: ListRep[A]): List[A] =
      r match {
        case Inl(_) => Nil
        case Inr(Product(x, xs)) => x :: xs
      }

    def from = fromList

    def to = toList
  }

  implicit def rList[A, G[_]](implicit gg: Generic[G], g: G[A]): G[List[A]] = {
    gg.view(listIso[A], () => gg.plus(gg.unit, gg.product(g, rList[A, G](gg, g))))
  }

  implicit def GList[A, G[_]](implicit gg: Generic[G], a: GRep[G, A]): GRep[G, List[A]] = new GRep[G, List[A]] {
    def grep: G[List[A]] = rList(gg, a.grep)
  }

  /*
    RepList is used as a helper function for the general dispatcher.
    It uses the Generic class that just takes 1 parameter.
   */
  implicit def RepList[G[_]](implicit g: Generic[G]): FRep[G, List] = {
    new FRep[G, List] {
      def frep[A](g1: G[A]): G[List[A]] = rList(g, g1)
    }
  }

  /* frepListCurried is creates an instance for the general dispatcher FRep,
    which can be used by the functions extended by the Generic class that takes 1 parameter.
   The difference between frepList is that this function uses a lambda type, such that it can obtain the parameter B when the frep function is called.
   */
  implicit def frepListCrush[A,B,G[_,_]](g : G[B, A])(implicit gg : Generic[({type AB[A] = G[B,A]})#AB]): G[B, List[A]] = {
    gg.view(listIso[A], () => gg.plus(gg.unit, gg.product(g, frepListCrush[A, B, G](g)(gg))))
  }
  /*
      frep2List is used as a helper function for the general dispatcher. It uses the Generic2 class that just takes 2 parameters.
      It is explicit uses for the Map function. Unfortunately, we could not solve to it using a general parameter.
     */
  def frep2List[A, B, G[_, _]](g: G[A, B])(implicit gg: Generic2[G]): G[List[A], List[B]] = {
    gg.view(listIso[A], listIso[B], () => gg.plus(gg.unit, gg.product(g, frep2List[A, B, G](g))))
  }

  implicit val Rep2List = new Base.FRep2[Functions.Map, List] {
    def frep2[A, B](g1: Map[A, B]): Map[List[A], List[B]] = {
      frep2List(g1)
    }
  }
}
