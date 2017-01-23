package Base

import Generic._

object Instances {

  object List {
    def fromList[A]: (List[A] => Plus[Unit, Product[A, List[A]]]) = l =>
      l match {
        case Nil => Inl(Unit)
        case (x :: xs) => Inr(Product(x, xs))
      }

    def toList[A]: (Plus[Unit, Product[A, List[A]]] => List[A]) = r =>
      r match {
        case Inl(_) => Nil
        case Inr(Product(x, xs)) => x :: xs
      }

    def listIso[A] = new Iso[List[A], Plus[Unit, Product[A, List[A]]]] {
      def from = fromList
      def to = toList
    }

    def rList[A, G[_]](g: G[A])(implicit gg: Generic[G]): G[List[A]] = {
      gg.view(listIso[A], () => gg.plus(gg.unit, gg.product(g, rList[A, G](g))))
    }

    class GenericList[G[_]](implicit gg: Generic[G]) {
      def list[A](x : G[A]) : G[List[A]] = rList(x)
    }

    class GList[A, G[_]](implicit glg: GenericList[G], a: GRep[G, A]) extends GRep[G, List[A]] {
      def grep: G[List[A]] = glg.list[A](a.grep)
    }
  }

  object Either {
    def EitherIso[A, B] = new Iso[Either[A, B], Plus[A, B]] {
      def from: (Either[A, B] => Plus[A, B]) = e => e match {
        case Left(x) => Inl(x)
        case Right(x) => Inr(x)
      }

      def to: (Plus[A, B] => Either[A, B]) = p => p match {
        case Inl(x) => Left(x)
        case Inr(x) => Right(x)
      }
    }
  }
}