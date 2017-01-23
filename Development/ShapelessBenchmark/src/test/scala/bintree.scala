import org.scalatest.FlatSpec
import types.{Bin, Leaf, BinTree}

import benchmark.Eq._
import benchmark.Show._
import benchmark.Collect._

object BinTreeTest extends FlatSpec {

  val l1 : BinTree[Int] = Bin(Bin(Leaf(1), Leaf(2)), Leaf(3))
  val l2 : BinTree[Int] = Bin(Bin(Leaf(1), Leaf(2)), Leaf(3))
  val l3 : BinTree[Int] = Leaf(1)

  "geq" should "be equal for equal trees" in {
    assert(geq(l1,l1))
    assert(geq(l1,l2))
  }

  "geq" should "not be equal on different trees" in {
    assert(!geq(l1,l3))
  }


}