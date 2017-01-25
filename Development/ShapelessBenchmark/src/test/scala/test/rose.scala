package test

import org.scalatest.FlatSpec
import benchmark.Eq._
import types.{Node, Rose}

class RoseTest extends FlatSpec{

  val l1 : Rose[Int] = Node(1,List())
  val l2 : Rose[Int] = Node(2,List(l1))
  val l3 : Rose[Int] = Node(2,List(l1))

  geq(l1,l1)  // l1's should be equal
  geq(l2,l1)  // l2 and l1 should be not equal
  geq(l3,l2)  // l3 and l2 should be equal
}