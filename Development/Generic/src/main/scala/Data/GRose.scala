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
  //General representation of the General rose case class
  type GRoseRep[F[_],A] = Product[A,F[GRose[F,A]]]

  def fromGrose[F[_],A](tree : GRose[F,A]): GRoseRep[F,A] ={
    tree match{
      case GNode(v,r) => Product(v,r)
    }
  }
  //from general representaion of Grose to GRose
  def toGrose[F[_],A](tree : GRoseRep[F,A] ): GRose[F,A] = {
    tree match {
      case Product(v, r) => GNode(v, r)
    }
  }

  //isomorphic function
  def isoGrose[F[_],A] = new Iso[GRose[F,A],Product[A,F[GRose[F,A]]]]{
    override def from: GRose[F,A] => GRoseRep[F,A] = fromGrose

    override def to: GRoseRep[F,A] => GRose[F,A] = toGrose
  }

  //Represention dispatcher for GCrush. In order to use this representation we have to create a function that uses GRep
  implicit def gCrush[A, G[_],F[_]](implicit gg: Generic[G], a1: GRep[G, A],a2: GRep[G, F[GRose[F,A]]]) = new GRep[G, GRose[F,A]] {
    override def grep: G[GRose[F, A]] = gRosetree1(a1.grep)(a2.grep)
  }


  def gRosetree1[A,G[_],F[_]](a : G[A])(f : G[F[GRose[F,A]]])(implicit gg : Generic[G]) = {
    gg.view(isoGrose[F,A],() => gg.product(a,f))
  }

  def gRosetree2[A,B,G[_,_],F[_]](a : G[A,B])(f : G[F[GRose[F,A]],F[GRose[F,B]]])(implicit gg : Generic2[G]) = {
    gg.view(isoGrose[F,A],isoGrose[F,B],() => gg.product(a,f))
  }
}

/*
The below code to implement a representation of a more concreter GRose
 */
object RoseObject{
  type RoseRep[A] = Product[A,List[Rose[A]]]

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

  def isoRose[A] = new Iso[Rose[A],RoseRep[A]]{
    override def from: Rose[A] => RoseRep[A] = fromRose

    override def to: RoseRep[A] => Rose[A] = toRose
  }

  def roseTree1[A,G[_]](a : G[A])(f : G[List[Rose[A]]])(implicit gg : Generic[G]) = {
    gg.view(isoRose[A],() => gg.product(a,f))
  }


  def roseTree2[A,B,G[_,_]](a : G[A,B])(f : G[List[Rose[A]],List[Rose[B]]])(implicit gg : Generic2[G]) = {
    gg.view(isoRose[A],isoRose[B],() => gg.product(a,f))
  }
}


