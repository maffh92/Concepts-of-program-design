package test

import org.scalatest.FlatSpec
import Functions.Crush
import Functions.Crush._
import Base.Ops.IntPlusMonoid

class CrushTest extends FlatSpec{
  import Data.List._

  implicit val m = IntPlusMonoid

  "crush" should "fold over the list" in {
    assert(crushlM(List(5,3,10))==18)
    assert(crushlM(List(5,3,12))==150)
  }
}
