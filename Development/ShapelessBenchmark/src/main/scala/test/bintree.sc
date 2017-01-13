object BinTreeTest {
  import types.{Bin, Leaf, BinTree}

  import benchmark.Eq._
  import benchmark.Show._

  val l1 : BinTree[Int] = Bin(Bin(Leaf(1), Leaf(2)), Leaf(3))
  val l2 : BinTree[Int] = Bin(Bin(Leaf(1), Leaf(2)), Leaf(3))
  val l3 : BinTree[Int] = Leaf(1)

  geq(l1,l1)  // l1's must be equal
  geq(l1,l2)  // l1 and l2 must be equal
  geq(l1,l3)  // l1 and l3 must be different

  //gshow(l1)
}