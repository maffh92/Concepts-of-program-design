package Data

import Base._
import scala.language.{higherKinds, postfixOps}

sealed trait GRose[F[_],A]
case class GNode[F[_],A](V : A, r : F[GRose[F,A]]) extends GRose[F,A]

object GRose{
  // Generic representation of GRose
  type GRoseRep[F[_],A] = Product[A,F[GRose[F,A]]]

  // Isomorphism between GRose and its generic representation.
  def GRoseIso[F[_],A] : Iso[GRose[F,A],GRoseRep[F,A]] = new Iso[GRose[F,A],GRoseRep[F,A]]{
    def fromGRose[F[_],A](tree : GRose[F,A]): GRoseRep[F,A] = {
      tree match{
        case GNode(v,r) => Product(v,r)
      }
    }
    def toGRose[F[_],A](tree : GRoseRep[F,A] ): GRose[F,A] = {
      tree match {
        case Product(v, r) => GNode(v, r)
      }
    }

    def from: GRose[F,A] => GRoseRep[F,A] = fromGRose
    def to: GRoseRep[F,A] => GRose[F,A]   = toGRose
  }

  //Represention dispatcher for GCrush. In order to use this representation we have to create a function that uses GRep
  implicit def gCrush[A, G[_],F[_]](implicit gg: Generic[G], a1: GRep[G, A],a2: GRep[G, F[GRose[F,A]]]) = new GRep[G, GRose[F,A]] {
    override def grep: G[GRose[F, A]] = gRosetree1(a1.grep)(a2.grep)
  }


  def gRosetree1[A,G[_],F[_]](a : G[A])(f : G[F[GRose[F,A]]])(implicit gg : Generic[G]) = {
    gg.view(GRoseIso[F,A],() => gg.product(a,f))
  }

  def gRosetree2[A,B,G[_,_],F[_]](a : G[A,B])(f : G[F[GRose[F,A]],F[GRose[F,B]]])(implicit gg : Generic2[G]) = {
    gg.view(GRoseIso[F,A],GRoseIso[F,B],() => gg.product(a,f))
  }
}
