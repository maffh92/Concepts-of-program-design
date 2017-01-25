package benchmark

import shapeless.{:: => :::, _}
import shapeless.ops.hlist
import shapeless.ops.coproduct
import types.{Salary}

trait CollectSalary[A] {
  def apply(x : A) : List[Salary]
}

trait LowPriorityCollect {
  implicit def genericCollect[A, T]
  (implicit
   ga: Generic.Aux[A, T],
   c:  Lazy[CollectSalary[T]]
  ): CollectSalary[A] = new CollectSalary[A] {
    def apply(x: A) = c.value(ga.to(x))
  }
}
object Collect extends LowPriorityCollect {

  implicit val elemCollect: CollectSalary[Salary] = new CollectSalary[Salary] {
    def apply(x: Salary): List[Salary] = List(x)
  }

  /*implicit def notElemCollect[A]: CollectSalary[A] = new CollectSalary[A] {
    def apply(x: A) : List[Salary] = List()
  }*/

  /*implicit def notElemCollect[A,B]: Collect[A,B] = new Collect[A,B] {
    def apply(x: A) : List[B] = List()
  }*/

  implicit def listCollect[A](implicit s: CollectSalary[A]): CollectSalary[List[A]] =
    new CollectSalary[List[A]] {
        def apply(xs : List[A]) : List[Salary] = xs.map(s.apply(_)).flatten
    }

  implicit def hnilCollect[A]: CollectSalary[HNil] = new CollectSalary[HNil] {
    def apply(x: HNil): List[Salary] = List()
  }

  implicit def hlistCollect[H, T <: HList]
    (implicit
     hc: Lazy[CollectSalary[H]],
     tc: Lazy[CollectSalary[T]]
    ): CollectSalary[H ::: T] = new CollectSalary[H ::: T] {
      def apply(x: H ::: T): List[Salary] = hc.value(x.head) ++ tc.value(x.tail)
    }

    implicit val cnilCollect: CollectSalary[CNil] = new CollectSalary[CNil] {
      def apply(c: CNil): List[Salary] = List()
    }

    implicit def coproductCollect[H, T <: Coproduct]
    (implicit
     hc: Lazy[CollectSalary[H]],
     tc: Lazy[CollectSalary[T]]
    ): CollectSalary[H :+: T] = new CollectSalary[H :+: T] {
      def apply(c: H :+: T): List[Salary] = c match {
        case Inl(h) => hc.value(h)
        case Inr(t) => tc.value(t)
      }
    }

  implicit class CollectWrapper[A](a: A) {
    def collect(implicit c: CollectSalary[A]) = c(a)
  }
}

/*
object Collect {
  def apply[A,B](implicit c: Collect[A,B]): Collect[A,B] = c
h
  implicit def genericCollect[A, B, P <: Poly2, T <: HList]
  (implicit
   ga: Generic.Aux[A, T],
   fold: hlist.LeftFolder.Aux[T, List[B], P, List[B]]
  ): Collect[A, B] = new Collect[A,B] {
      def apply(x: A): List[B] = fold.apply(ga.to(x),List())
    }

  implicit def hnilCollect[A]: Collect[HNil, A] = new Collect[HNil, A] {
    def apply(x: HNil): List[A] = List()
  }

  implicit def hlistCollect[H, T <: HList, B]
  (implicit
   hc: Collect[H, B],
   tc: Collect[T, B]
  ): Collect[H :: T, B] = new Collect[H :: T, B] {
    def apply(x: H :: T): List[B] = hc(x.head) ++ tc(x.tail)
  }

  implicit def cnilCollect[A]: Collect[CNil, A] = new Collect[CNil, A] {
    def apply(c: CNil): List[A] = List()
  }

  implicit def coproductCollect[H, T <: Coproduct, B]
  (implicit
   hc: Collect[H, B] = null,
   tc: Collect[T, B]
  ): Collect[H :+: T, B] = new Collect[H :+: T, B] {
    def apply(c: H :+: T): List[B] = c match {
      case Inl(h) => hc(h)
      case Inr(t) => tc(t)
    }
  }

  implicit class CollectOps[A](a: A) {
    class Builder[B] {
      def apply[P <: Poly2](poly: P)(implicit c: Collect[A,B]): List[B] =
        c.apply(a)
    }
    def collect[B]: Builder[B] = new Builder[B]
  }

}
*/