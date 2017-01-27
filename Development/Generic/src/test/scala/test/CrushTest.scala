package test

import org.scalatest.FlatSpec
import Functions.Crush
import Data.Tree._
import Functions.Crush._
import Base.Ops.IntPlusMonoid
import Data.{Bin, BinTree, Fork, Leaf, Perfect, Succ, Zero}


class CrushTest extends FlatSpec{
  import Data.List._

//  implicit val m = IntPlusMonoid
//
//  "crush" should "fold over the list" in {
//    assert(crushlM(List(5,3,10))==18)
//    assert(crushlM(List(5,3,12))==150)
//  }

  val bintree : BinTree[Int] = Bin(Bin(Leaf(1),Leaf(2)),Bin(Leaf(3),Leaf(4)))
  "crush" should "sum and product over the list and trees" in {
        assert(sum(List(5,3,10))==18)
        assert(product(List(5,3,12))==180)
        assert(sum(bintree)==10)
        assert(product(bintree)==24)
      }


}
