import benchmark.{Eq, Show}
import shapeless.Generic

object PTreeTest {
  import types.{Perfect, Zero, Succ}
  import benchmark.Eq._
  import benchmark.Show._

  val l1 : Perfect[Int] = Zero(1)
  val g1 : Generic[Perfect[Int]] = Generic[Perfect[Int]]

  // Shapeless is able to automatically derive a Generic instance
  // for Perfect tree but instead refuses to use such instance to
  // actually derive the instance for the Show typeclass.

  // val s1 : Show[Perfect[Int]] = Show[Perfect[Int]]

  // It happens the same for the Eq typeclass, but sometimes in the Scala
  // compiler diverges.

  // val e1 : Eq[Perfect[Int]] = Eq[Perfect[Int]

  println("Not impl")
}
