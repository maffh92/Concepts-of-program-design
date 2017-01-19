package Data

import Base.GenericObject.{GRep, _}
/**
  * Created by maffh on 19-1-17.
  */
sealed trait Rose[A]
case class Node[A](v : A, r : List[Rose[A]]) extends Rose[A]

sealed trait GRose[F[_],A]
case class GNode[F[_],A](V : A, r : F[GRose[F,A]]) extends GRose[F,A]


object GRose{
  type GRoseRep[F[_],A] = Product[A,F[GRose[F,A]]]

  def fromGrose[F[_],A](tree : GRose[F,A]): GRoseRep[F,A] ={
    tree match{
      case GNode(v,r) => Product(v,r)
    }
  }

  def toGrose[F[_],A](tree : GRoseRep[F,A] ): GRose[F,A] = {
    tree match {
      case Product(v, r) => GNode(v, r)
    }
  }

  def func[A,F[_],G[_]](f : G[A])(a : G[F[GRose[F,A]]]) : Int = 2
  def isoGrose[F[_],A] = new Iso[GRose[F,A],Product[A,F[GRose[F,A]]]]{
    override def from: GRose[F,A] => GRoseRep[F,A] = fromGrose

    override def to: GRoseRep[F,A] => GRose[F,A] = toGrose
  }
//
  def gCrush[A, G[_],F[_]](implicit gg: Generic[G], a1: GRep[G, A],a2: GRep[G, F[GRose[F,A]]]) = new GRep[G, GRose[F,A]] {
    override def grep: G[GRose[F, A]] = rosetree(a1.grep)(a2.grep)
  }


  def rosetree[A,G[_],F[_]](a : G[A])(f : G[F[GRose[F,A]]])(implicit gg : Generic[G]) = {
    gg.view(isoGrose[F,A],() => gg.product(a,f))
  }
}
