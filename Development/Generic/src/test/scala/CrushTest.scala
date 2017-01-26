import Base._
import Functions.Collect
import org.scalatest.FlatSpec

class CrushTest extends FlatSpec{
  import Data.List._

  //test the sum function
  assert(Functions.Crush.sum(List(5,3,10))==18)
  //test the product function
  assert(Functions.Crush.product(List(5,3,10))==150)
}
