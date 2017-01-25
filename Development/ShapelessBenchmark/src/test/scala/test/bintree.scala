package test

import org.scalatest.FlatSpec
import shapeless._
import poly._
import types.{Bin, BinTree, Leaf}
import benchmark.Eq._
import benchmark.Collect._
import benchmark.Collect

class BinTreeTest extends FlatSpec {

  val l1 : BinTree[Int] = Bin(Bin(Leaf(1), Leaf(2)), Leaf(3))
  val l2 : BinTree[Int] = Bin(Bin(Leaf(1), Leaf(2)), Leaf(3))
  val l3 : BinTree[Int] = Leaf(1)

  //val collectBinTree : Collect[BinTree[Int], Int] = Collect[BinTree[Int],Int]

  "geq" should "be equal for equal trees" in {
    assert(geq(l1,l1))
    assert(geq(l1,l2))
  }

  "geq" should "not be equal on different trees" in {
    assert(!geq(l1,l3))
  }

  "transforming every integer" should "result in the transformed tree" in {
    object incr extends ->((x : Int) => x + 1)
    assert(everywhere(incr)(l3) == Leaf(2))
    assert(everywhere(incr)(l1) == Bin(Bin(Leaf(2), Leaf(3)), Leaf(4)))

  }

  "collect" should "get all integers from the tree" in {
    object myPoly extends Poly2 {
      implicit val intCase : Case.Aux[List[Int], Int, List[Int]] = at((acc,x) => x :: acc)
    }
    //val c = Collect[BinTree[Int], Int]
    //assert(l1.collect[Int](myPoly)==List(1,2,3))
  }


}