package test

import org.scalatest.FlatSpec
import Data.List._

class CrushTest extends FlatSpec{
  //test the sum function
  assert(Functions.Crush.sum(List(5,3,10))==18)
  //test the product function
  assert(Functions.Crush.product(List(5,3,10))==150)
}
