package test

import org.scalatest.FlatSpec
import types.{Perfect, Succ, Zero}
import benchmark._
import shapeless.PolyDefns.->
import shapeless._

class PTreeTest extends FlatSpec {

  val l1 : Perfect[Int] = Zero(1)
  val l2 : Perfect[String] = Succ(Zero(("Node1", "Node2")))

  val g1 : Generic[Perfect[Int]] = Generic[Perfect[Int]]

  // Shapeless is able to automatically derive a Generic instance
  // for Perfect tree but instead refuses to use such instance to
  // actually derive the instance for the Show typeclass.

  // val s1 : Show[Perfect[Int]] = Show[Perfect[Int]]

  // It happens the same for the Eq typeclass, but sometimes in the Scala
  // compiler diverges.

  // val e1 : Eq[Perfect[Int]] = Eq[Perfect[Int]

  "Append" should "append to all nodes" in {
    val afterAppend : Perfect[String] = Succ(Zero(("I'm Node1", "I'm Node2")))
    object append extends ->((x : String) => "I'm " + x)

    assert(everywhere(append)(l2) == afterAppend)
  }

}
