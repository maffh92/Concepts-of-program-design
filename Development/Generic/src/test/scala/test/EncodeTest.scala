package test

import Base._
import Data.GList._
import Functions.Encode._
import Functions.{Bit, Zero, One}
import org.scalatest.FlatSpec

class EncodeTest extends FlatSpec {
  // We need to explicitly name the result type to pass it to
  // encode, otherwise implicit resolution fails.
  type R = List[Plus[Unit,String]]

  "encode" should "properly encode a list of either unit string" in {
    assert(encode[List[Plus[Unit, String]]](List(Inl(Unit), Inr("0101"))) == List(One, Zero, One, One, Zero, One, Zero, One, Zero))
  }

}
