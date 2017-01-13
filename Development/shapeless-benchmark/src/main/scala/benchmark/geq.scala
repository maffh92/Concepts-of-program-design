package benchmark

import shapeless.{:+:, CNil, Coproduct, Generic, HList, HNil, Inl, Inr, Lazy, :: => :::}

/* Trait representing the Eq typeclass */
trait Eq[A] {
  def eq(x : A, y : A) : Boolean
}

/* Companion object of Eq */
object Eq {
  def apply[A](implicit e : Eq[A]) : Eq[A] = e

  def instance[A](func : (A, A) => Boolean) : Eq[A] =
    new Eq[A] {
      def eq(x : A,y : A) : Boolean =
        func(x,y)
    }

  /* Instances for common types */
  implicit object IntEq extends Eq[Int] {
    def eq(x : Int, y : Int) = x == y
  }

  implicit object StringEq extends Eq[String] {
    def eq(x : String, y : String) = x == y
  }

  /* This implementation is not found by implicit resolution
   * There are some issues with Covariance and using Nothing as
   * type parameter for example:
   * > geq(List(), List()) */
  implicit object NothingEq extends Eq[Nothing] {
     def eq(x : Nothing, y : Nothing) = true
  }

/*  implicit def ListEq[A](implicit eqa : Eq[A]) = new Eq[List[A]] {
    def eq(x : List[A], y : List[A]) = (x,y) match {
      case (Nil,Nil) => true
      case (x :: xs, y :: ys) => x == y && eq(xs,ys)
      case _ => false
    }
  }*/

  /* Instances for HList */
  implicit val hnilEq: Eq[HNil] =
    instance((_,_) => true)

  implicit def hlistEq[H, T <: HList](
    implicit
      hEq : Lazy[Eq[H]],
      tEq : Eq[T]): Eq[H ::: T] =
        instance((xs,ys) => (xs,ys) match {
          case (h ::: t, h1 ::: t1)  => hEq.value.eq(h,h1) && tEq.eq(t,t1)
        })

  /* Instances for Coproduct */
  implicit val cnilEq : Eq[CNil] =
    instance((_,_) => true)

  implicit def coproductEq[H, T <: Coproduct](
    implicit
      hEq : Lazy[Eq[H]],
      tEq : Eq[T]
  ) : Eq[H :+: T] =
    instance((xs,ys) => (xs,ys) match {
      case (Inl(h) , Inl(h1)) => hEq.value.eq(h,h1)
      case (Inr(t) , Inr(t1)) => tEq.eq(t,t1)
      case (Inr(_),Inl(_)) => false
      case (Inl(_),Inr(_)) => false
    })

  implicit def genericEq[A, R](
    implicit
      gen: Generic.Aux[A, R],
      env: Lazy[Eq[R]]
  ) : Eq[A] = instance((x,y) => env.value.eq(gen.to(x),gen.to(y)))

  def geq[A](x : A, y : A)(implicit e : Eq[A]) = e.eq(x,y)
}