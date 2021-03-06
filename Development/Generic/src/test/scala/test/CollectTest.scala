package test

import Base._
import Data.{C, Company, D, DU, E, P, PU, S, Salary}
import org.scalatest.FlatSpec

import scalaz.std.list._

class CollectTest extends FlatSpec {
  import Data.List._
  import Data.Either._
  import Functions.Collect
  import Functions.Collect._

  // We have to declare a type synonym in the top level
  // so Scala implicit mechanism is able to automatically find out instances.
  type C[X] = Collect[List,Int,X]
  type R = List[Either[Int,Char]]
  // GRep (Collect List Int) Int
  //implicit val gi  : GRep[C,Int] = GRepCollect[List,Int]

  // GRep (Collect List Int) Char
  //val gc = implicitly[GRep[C,Char]]

  // Generic[Collect[List,Int]
  //val genc = implicitly[Generic[({type C[X] = Collect[List,Int,X]})#C]]

  // GRep (Collect List Int) (Product Int Char)
  //val gp = implicitly[GRep[C,Plus[Int,Char]]] = GProduct[Int,Char,C](genc,gi,gc)

  // GRep (Collect List Int) List[Product[[Generic[C]]Int,Char]]
  //implicit val gl = implicitly[GRep[C,R]]

  "collect" should "get all int from the list" in {
    assert(collect[List,Int,List[Either[Int,Char]]](List(Left(1), Right('c'), Left(2))) == List(1, 2))
  }

  val e1 = E(P("Matthew", "Amsterdam"),S(1000))
  val e2 = E(P("Carlos", "Utrecht"),S(1500))
  val e3 = E(P("Renate", "Almeere"),S(2000))
  val e4 = E(P("Ferdinand", "Amsterdam"),S(3000))

  val d0 = D("Generic programming", e1, List())
  val d1 = D("Software", e2, List(DU(d0), PU(e3)))
  val d2 = D("Management", e4, List(PU(e4)))

  val c1 = C(List(d1,d2))
  "Collect" should "get all salaries from the list" in {

//    assert(collect[List,Salary,Company](c1) == List(1000,1500,2000,3000).map(S))
    //assert(collect[List,Int,List[Either[Int,Char]]](List(Left(1), Right('c'))) == List(1))
    //assert(collect[List,Int,List[Int]](List(1))==List(1))

    //val gl = Generic[List]
  }
}
