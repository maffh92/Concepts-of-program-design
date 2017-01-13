package benchmark

import shapeless.{:+:, :: => :::, CNil, Coproduct, Generic, HList, HNil, Inl, Inr, Lazy}

/* Typeclass Show */
trait Show[A] {
  def show(s : A) : String
}

/* Companion object of Show */
object Show {
  //def apply[A](implicit e : Show[A]) : Show[A] = e

  def instance[A](func : A => String) : Show[A] =
    new Show[A] {
      def show(x : A) : String = func(x)
    }

  /* Instances for common types */
  implicit object IntShow extends Show[Int] {
    def show(x : Int) : String = x.toString()
  }

  implicit object StringShow extends Show[String] {
    def show(x : String) : String = x
  }

  implicit def ShowTuple[A,B](implicit sa : Show[A], sb : Show[B]) = new Show[(A,B)] {
    def show(t : (A,B)) : String = "(" + sa.show(t._1) + "," + sb.show(t._2) + ")"
  }
  implicit def ListShow[A](implicit sh : Show[A]) = new Show[List[A]] {
    def show(x : List[A]) = x match {
      case Nil => "[]"
      case (x :: xs) => sh.show(x) ++ "::" ++ show(xs)
    }
  }

  /* Instances for HList */
  implicit val hnilShow: Show[HNil] =
    instance(x => "dummy")

  implicit def hlistShow[H, T <: HList]
  (implicit
     hShow : Lazy[Show[H]],
     tShow : Show[T]): Show[H ::: T] =
        instance(xs => xs match {
          case (h ::: t)  => hShow.value.show(h) ++ tShow.show(t)
        })

  /* Instances for Coproduct */
  implicit val cnilShow : Show[CNil] =
    instance(x => "dummy")

  implicit def coproductShow[H, T <: Coproduct]
  (implicit
    hShow: Lazy[Show[H]],
    tShow: Show[T]
  ) : Show[H :+: T] =
    instance(xs => xs match {
      case Inl(h) => hShow.value.show(h)
      case Inr(t) => tShow.show(t)
    })

  implicit def genericShow[A, R]
    (implicit
     gen: Generic.Aux[A, R],
     env: Show[R]
    ) : Show[A] = instance(x => env.show(gen.to(x)))

  def gshow[A](x : A)(implicit e : Show[A]) = e.show(x)
}