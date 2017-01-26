package test

import Base.GRep
import org.scalatest.FlatSpec
import Functions.Everywhere
import Functions.Everywhere._
import Data.List._
import Data.Either._

class EverywhereTest extends FlatSpec {
  type R = List[Either[Int,Char]]
  type C[X] = Everywhere[Int,X]

  implicit val i = implicitly[GRep[C,R]]

  //implicitly[G](implicit e: G) = e
  val f = (x : Int) => x + 1
  //implicit val i1 = GRepEverywhere(f)

  "everywhere" should "act like map" in {
    assert(everywhere[Int,R](f, List(Left(1),Right('c'),Left(2))) == List(Left(2),Right('c'),Left(3)))
  }

}
