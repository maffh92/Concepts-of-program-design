object GRoseTest {
  import shapeless.{Generic, everywhere}

  import types.{GRose}
  import benchmark.Eq._
  import benchmark.Show._

  // Explicit Generic instance for GRose
  val gengrose = Generic[GRose[List,Int]]

  // We should give a explicit type to GRose, as apparently the Scala
  // compiler has serious issues performing type inference in the presence
  // of Higher-Order Kinded types.
  val el : List[GRose[List,Int]] = List()
  val l1 : GRose[List,Int] = GRose[List,Int](1,List(GRose(2,el)))
  val l2 : GRose[List,Int] = GRose[List,Int](1,List(GRose(3,el)))

  geq(l1,l1) // l1's should be equal
  geq(l1,l2) // l1 and l2 should not be equal

  //gshow(l1)
}