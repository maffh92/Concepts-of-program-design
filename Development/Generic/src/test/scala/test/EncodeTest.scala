package test

import Base._


import Data.List._
import Data.Either._

import Functions.Encode._
import Functions.{Bit, Zero, One}
import org.scalatest.FlatSpec

class EncodeTest extends FlatSpec {
  // We need to explicitly name the result type to pass it to
  // encode, otherwise implicit resolution fails.
  type R = List[Either[Unit,String]]

  "encode" should "properly encode a list of either unit string" in {
    assert(encode[List[Either[Unit, String]]](List(Left(Unit), Right("0101"))) == List(One, Zero, One, One, Zero, One, Zero, One, Zero))
  }

}
