package test

import Data.List._
import Data.PerfectTree._
import Data.Tree._
import Data.{Bin, BinTree, Fork, Leaf, Perfect, Succ, Zero}
import Functions.Map._
import org.scalatest.FlatSpec
import Data.PerfectTree._
import Data.Perfect
import Data.Zero
import Data.Fork
import Data.Succ
import Data.Tree._
import Data.List._
import Data.BinTree
import Data.Bin
import Data.Leaf
import Functions.Map._

class MapTest extends FlatSpec{
  //increase function is used in the asserts to test if the map functions works
  def increase(x : Int) = x + 1

  //Binary tree test
  val bintree : BinTree[Int] = Bin(Bin(Leaf(1),Leaf(2)),Bin(Leaf(3),Leaf(4)))

  "map increase" should "increase each of the leaves of the tree" in {
    assert(map(increase)(bintree) == Bin(Bin(Leaf(2), Leaf(3)), Bin(Leaf(4), Leaf(5))))
  }
  //perfecttree test
  val perfectTree : Perfect[Int] = Succ(Zero(Fork(1,2)))
  "map increase" should "increase each of the values in the perfect tree" in {
    assert(map(increase)(perfectTree) == Succ(Zero(Fork(2, 3))))
  }

  //List test
  "map increase" should "increase each of the values in the list" in {
    assert(map(increase)(List(1, 2)) == List(2, 3))
    assert(map(increase)(List(1, 10, 3, 100)) == List(2, 11, 4, 101))
  }
}
