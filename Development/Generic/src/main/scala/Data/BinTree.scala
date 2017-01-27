package Data

import Base._

/*
* This file is used for the representation of a binary tree.
* We have implemented the FRep and FRep2. This means that Binary tree supports the function that are
* implemented by either the Generic or Generic2 class
*/

sealed trait BinTree[T]

case class Leaf[T](leaf : T) extends BinTree[T]
case class Bin[T](left : BinTree[T], right : BinTree[T]) extends BinTree[T]

object GBinTree{

  //General representation
  type BinTreeRep[T] = Plus[T,Product[BinTree[T],BinTree[T]]]

  /*
  The fromBinTree and ToBinTree functions are used as a isomorphic function. It is implemented in the BinTreeIso
   */
  def fromBinTree[T](tree : BinTree[T]) : BinTreeRep[T] = {
    tree match{
      case Leaf(x) => Inl(x)
      case Bin(l,r) => Inr(Product(l,r))
    }
  }

  def toBinTree[T](plus : BinTreeRep[T]) : BinTree[T] = {
    plus match{
      case Inl(x) => Leaf(x)
      case Inr(Product(l,r)) => Bin(l,r)
    }
  }

  def binTreeIso[T] = new Iso[BinTree[T],BinTreeRep[T]]{
    override def from = fromBinTree
    override def to = toBinTree
  }


  //The bintree is used for the general dispatcher for generic with 1 parameter.
  def binTree1B[A,B,G[_,_]](g : G[B, A])(implicit gg : Generic[({type AB[A] = G[B,A]})#AB]): G[B, BinTree[A]] = {
    gg.view(binTreeIso[A],() => gg.plus(g,gg.product(binTree1B(g),binTree1B(g))))
  }
  implicit def frepTreeB[B,G[_,_]](implicit g : Generic[({type AB[A] = G[B,A]})#AB]) : Base.FRep[({type AB[A] = G[B,A]})#AB,BinTree] = {
     new FRep[({type AB[X] = G[B, X]})#AB, BinTree] {
       override def frep[A](g1: G[B, A]): G[B, BinTree[A]] = binTree1B(g1)(g)
     }
  }


  //The bintree is used for the general dispatcher for generic with 1 parameter.
  def binTree1[A,G[_]](g : G[A])(implicit gg : Generic[G]): G[BinTree[A]] = {
    gg.view(binTreeIso[A],() => gg.plus(g,gg.product(binTree1[A,G](g),binTree1[A,G](g))))
  }
  implicit def frepTree[G[_]](implicit g : Generic[G]) : FRep[G,BinTree] = {
    new FRep[G,BinTree] {
      override def frep[A](g1 : G[A]) : G[BinTree[A]] = {
        binTree1(g1)
      }
    }
  }


   //The bintree and frep2Tree is used for the general dispatcher for generic with 2 parameter.
  def binTree2[A,B,G[_,_]](g : G[A,B])(implicit gg : Generic2[G]): G[BinTree[A],BinTree[B]] = {
    gg.view(binTreeIso[A],binTreeIso[B],() => gg.plus(g,gg.product(binTree2[A,B,G](g),binTree2[A,B,G](g))))
  }
  implicit def frep2Tree[G[_,_]](implicit g : Generic2[G]) : FRep2[G,BinTree] = {
    new FRep2[G,BinTree] {
      override def frep2[A, B](g1: G[A, B]): G[BinTree[A], BinTree[B]] = binTree2(g1)
    }
  }



  /*
  gTree is used for the general distpacher for GRep
   */
  def gTree[A, G[_]](implicit gg: Generic[G], a: GRep[G, A]) = new GRep[G, BinTree[A]] {
    override def grep: G[BinTree[A]] = binTree1(a.grep)
  }

}
