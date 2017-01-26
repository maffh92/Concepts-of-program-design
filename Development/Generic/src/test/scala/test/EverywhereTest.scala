package test

import Base.GRep
import org.scalatest.FlatSpec
import Functions.Everywhere
import Functions.Everywhere._
import Data.List._
import Data.Either._

class EverywhereTest extends FlatSpec {
  //implicit val i = implicitly[GRep[C,R]]

  //implicitly[G](implicit e: G) = e
  val f = (x : Int) => x + 1
  //implicit val i1 = GRepEverywhere(f)

  "everywhere" should "act like map" in {
    assert(everywhere(f, List(Left(1),Right('c'),Left(2))) == List(Left(2),Right('c'),Left(3)))
    assert(everywhere(f,List(1,2,3)) == List(2,3,4))
  }

}
