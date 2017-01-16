package Data

import Base.GenericObject._

sealed trait BinTree[T]

case class Leaf[T](leaf : T) extends BinTree[T]
case class Bin[T](left : BinTree[T], right : BinTree[T]) extends BinTree[T]

object Tree{

  type BinTreeRep[T] = Plus[T,Product[BinTree[T],BinTree[T]]]
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


  def binTree1[A,G[_]](g : G[A])(implicit gg : Generic[G]): G[BinTree[A]] = {
    gg.view(binTreeIso[A],() => gg.plus(g,gg.product(binTree1[A,G](g),binTree1[A,G](g))))
  }

  def binTree2[A,B,G[_,_]](g : G[A,B])(implicit gg : Generic2[G]): G[BinTree[A],BinTree[B]] = {
    gg.view(binTreeIso[A],binTreeIso[B],() => gg.plus(g,gg.product(binTree2[A,B,G](g),binTree2[A,B,G](g))))
  }

  implicit def frepTree1[G[_]](implicit g : Generic[G]) : FRep[G,BinTree] = {
    new FRep[G,BinTree] {
      override def frep[A](g1 : G[A]) : G[BinTree[A]] = {
        binTree1(g1)
      }
    }
  }

  implicit def frepTree2[G[_,_]](implicit g : Generic2[G]) : FRep2[G,BinTree] = {
    new FRep2[G,BinTree] {
      override def frep2[A, B](g1: G[A, B]): G[BinTree[A], BinTree[B]] = binTree2(g1)
    }
  }

  def gTree[A, G[_]](implicit gg: Generic[G], a: GRep[G, A]) = new GRep[G, BinTree[A]] {
    override def grep: G[BinTree[A]] = binTree1(a.grep)
  }







}




//bintree a =  view isoBinTree (constr "Leaf" 1 a <|>
//constr "Bin" 2 (bintree a <*> bintree a))
//
//isoBinTree = Iso fromBinTree toBinTree
//
//fromBinTree (Leaf x)              =  Inl x
//fromBinTree (Bin l r)             =  Inr (l :*: r)
//
//toBinTree (Inl x)                 =  Leaf x
//toBinTree (Inr (l :*: r))         =  Bin l r
//
//instance FunctorRep BinTree where
//functorRep   =  bintree
//
//instance (Generic g, GRep g a) => GRep g (BinTree a) where
//over = bintree over


