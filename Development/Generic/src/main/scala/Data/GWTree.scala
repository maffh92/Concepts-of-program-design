package Data

import Base.GenericObject._

sealed class WTree[A,W]
case class WLeaf[A,W](a : A) extends WTree[A,W]
case class WBin[A,W](x : WTree[A,W], y : WTree[A,W]) extends WTree[A,W]
case class WithWeight[A,W](x : WTree[A,W], w : W) extends WTree[A,W]

object WTreeObjectRep{
  def rwtree[G[_],A,W](a : G[A], w : G[W])(implicit ggTree : GenericWTree[G],gg : Generic[G]) : G[WTree[A,W]] = {
    gg.view(isoTree,() => gg.plus(a,gg.plus(gg.product(ggTree.wtree(a,w),ggTree.wtree(a,w)),gg.product(ggTree.wtree(a,w),w))))
  }

  type WRTreeRep[A,W] = Plus[A,Plus[Product[WTree[A,W],WTree[A,W]],Product[WTree[A,W],W]]]

  def isoTree[A,W] : Iso[WTree[A,W],WRTreeRep[A,W]] = new Iso[WTree[A,W],WRTreeRep[A,W]] {
    override def from: (WTree[A, W]) => WRTreeRep[A, W] = fromTree

    override def to: (WRTreeRep[A, W]) => WTree[A, W] = toTree
  }

  def fromTree[A,W](tree : WTree[A,W]) : WRTreeRep[A,W] = tree match{
    case WLeaf(x) => Inl(x)
    case WBin(l,r) => Inr(Inl(Product(l,r)))
    case WithWeight(a,b) => Inr(Inr(Product(a,b)))
  }

  def toTree[A,W](tree : WRTreeRep[A,W]) :  WTree[A,W] = tree match{
    case Inl(x) => WLeaf(x)
    case Inr(Inl(Product(l,r))) => WBin(l,r)
    case Inr(Inr(Product(a,b))) => WithWeight(a,b)
  }


  class GenericWTree[G[_]](implicit gg : Generic[G]){
      def wtree[A,W](a : G[A],w : G[W]) :  G[WTree[A,W]] = {
        rwtree(a,w)(this,gg)
      }
  }

}