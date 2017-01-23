package benchmark

import shapeless.{:+:, :: => :::, CNil, Coproduct, Generic, HList, HNil, Inl, Inr, Lazy}

trait Collect[A,B] {
  def collect(x : A) : List[B]
}

object Collect {
    def apply[A, B](implicit e : Collect[A,B]) : Collect[A,B] = e

    def instance[A,B](func : A => List[B]) : Collect[A,B] = new Collect[A,B] {
        def collect(x : A) : List[B] = func(x)
      }

    implicit def idCollect[A] : Collect[A,A] = new Collect[A,A] {
      def collect(x : A) : List[A] = List(x)
    }

    /* Instances for HList */
    implicit def hnilCollect[B]: Collect[HNil,B] =
      instance(x => throw new RuntimeException())

    implicit def hlistCollect[H, T <: HList, B]
    (implicit
     hCollect : Lazy[Collect[H,B]],
     tCollect : Collect[T,B]): Collect[H ::: T,B] =
      instance(xs => xs match {
        case (h ::: t)  => hCollect.value.collect(h) ++ tCollect.collect(t)
      })

    /* Instances for Coproduct */
    implicit def cnilCollect[B] : Collect[CNil,B] =
      instance(x => throw new RuntimeException())

    implicit def coproductCollect[H, T <: Coproduct, B]
    (implicit
     hCollect: Lazy[Collect[H,B]],
     tCollect: Collect[T,B]
    ) : Collect[H :+: T,B] =
      instance(xs => xs match {
        case Inl(h) => hCollect.value.collect(h)
        case Inr(t) => tCollect.collect(t)
      })

    implicit def genericCollect[A, B, R]
    (implicit
     gen: Generic.Aux[A, R],
     env: Collect[R,B]
    ) : Collect[A,B] = instance(x => env.collect(gen.to(x)))

    def gCollect[A,B](x : A)(implicit e : Collect[A,B]) : List[B] = e.collect(x)



  }