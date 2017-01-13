package Functions

import Base.GenericObject._

object MapObject {

  trait Map[A,B]{
    def selMap : A => B
  }

  implicit object MapC extends Generic2[Map]{

    def idMap[A] : Map[A,A] = new Map[A,A]{def selMap = id}

    def unit = idMap
    def char = idMap
    def int  = idMap

    def product[A1,A2,B1,B2](ra : Map[A1,A2], rb : Map[B1,B2]) = {
      new Map[Product[A1,B1],Product[A2,B2]]{
        def selMap = rprodMap(ra)(rb)
      }
    }

    def plus[A1,A2,B1,B2](ra : Map[A1,A2], rb : Map[B1,B2]) = {
      new Map[Plus[A1,B1],Plus[A2,B2]]{
        def selMap = rsumMap(ra)(rb)
      }
    }

    def view[A1,A2,B1,B2](iso1 : Iso[A2,A1],iso2 : Iso[B2,B1],a: () => Map[A1,B1]) : Map[A2,B2] =
    {
      new Map[A2,B2]{
        def selMap = rTypeMap(iso1)(iso2)(a())
      }
    }


    def rprodMap[A1,A2,B1,B2](ra : Map[A1,A2])
                             (rb : Map[B1,B2])(product : Product[A1,B1]) : Product[A2,B2] = {
      product match{
        case Product(a,b) => Product(ra.selMap(a),rb.selMap(b))
      }
    }


    def rsumMap[A1,A2,B1,B2]
    (ra : Map[A1,A2])(rb : Map[B1,B2])(plus : Plus[A1,B1]) : Plus[A2,B2] = {
      plus match{
        case Inl(x) => Inl(ra.selMap(x))
        case Inr(x) => Inr(rb.selMap(x))
      }
    }



    def rTypeMap[B,R,D,A](iso1 : Iso[B,R])(iso2 : Iso[D,A])(ra : Map[R,A])(b : B) :  D = {
      iso2.to(ra.selMap(iso1.from(b)))
    }
  }

  def map[A,B,F[_]](f : A => B)(functor : F[A])(implicit rep : FRep2[Map, F]) : F[B] = {
    val fMap = new Map[A,B]{
      def selMap = f
    }
    return(rep frep2(fMap) selMap(functor))
  }


}
