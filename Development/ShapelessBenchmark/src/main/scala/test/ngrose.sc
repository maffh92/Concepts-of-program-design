object NGRoseTest {
  import types.{NGRose}

  import shapeless.Generic

  import benchmark.Eq._
  import benchmark.Show._

  // For a type like NGRose, shapeless is not able to automatically
  // derive a Generic instance.

  //val genngrose = Generic[NGRose[List,Int]]


}