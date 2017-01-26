package Data

import Base._

sealed trait Rose[A]
case class Node[A](v : A, r : List[Rose[A]]) extends Rose[A]

object Rose{
  // Generic representation of Rose tree.
  type RoseRep[A] = Product[A,List[Rose[A]]]

  def isoRose[A] = new Iso[Rose[A],RoseRep[A]]{
    def fromRose[A](tree : Rose[A]): RoseRep[A] ={
      tree match{
        case Node(v,r) => Product(v,r)
      }
    }

    def toRose[F[_],A](tree : RoseRep[A] ): Rose[A] = {
      tree match {
        case Product(v, r) => Node(v, r)
      }
    }
    def from: Rose[A] => RoseRep[A] = fromRose
    def to: RoseRep[A] => Rose[A] = toRose
  }

  def roseTree1[A,G[_]](a : G[A])(f : G[List[Rose[A]]])(implicit gg : Generic[G]) = {
    gg.view(isoRose[A],() => gg.product(a,f))
  }


  def roseTree2[A,B,G[_,_]](a : G[A,B])(f : G[List[Rose[A]],List[Rose[B]]])(implicit gg : Generic2[G]) = {
    gg.view(isoRose[A],isoRose[B],() => gg.product(a,f))
  }
}