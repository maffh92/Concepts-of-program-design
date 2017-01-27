package test

import org.scalatest.FlatSpec
import Data.GBinTree._
import Functions.Crush._
import Base.Ops.{IntPlusMonoid,IntProdMonoid}
import Data.{Bin, BinTree, Leaf}


class CrushTest extends FlatSpec{
  import Data.List._

  val bintree : BinTree[Int] = Bin(Bin(Leaf(1),Leaf(2)),Bin(Leaf(3),Leaf(4)))
  "crush" should "sum and product over the list and trees" in {
        assert(sum(List(5,3,10))==18)
        assert(product(List(5,3,12))==180)
        assert(sum(bintree)==10)
        assert(product(bintree)==24)
        assert(crushrM(bintree)(IntPlusMonoid)==10)
        assert(crushrM(bintree)(IntProdMonoid)==24)
      }


}
