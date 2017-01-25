package test

import org.scalatest.FlatSpec
import shapeless._
import poly._
import types._
import benchmark._
import benchmark.Collect._
import benchmark.Collect
import shapeless.ops.coproduct.LeftFolder

class CompanyTest extends FlatSpec {
  import benchmark.Eq._

  val genCompany = Generic[Company]

  val c1 =
    Company(
      List(Dept("Office2", "Matthew",
                List(PU(Employee(Person("Matthew", "Amsterdam"), Salary(2000)))
                    ,DU(Dept("Office1", "John", List(PU(Employee(Person("John",  "London"), Salary(1000))))))))
          ,Dept("Office3", "Renate", List(PU(Employee(Person("Carlos", "Spain"), Salary(2000)))
                                         ,PU(Employee(Person("Renate", "Almeere"), Salary(2000))))))
      )


  "Raising" should "raise the salary" in {
    val onceRaised =
      Company(
        List(Dept("Office2", "Matthew",
          List(PU(Employee(Person("Matthew", "Amsterdam"), Salary(3000)))
            ,DU(Dept("Office1", "John", List(PU(Employee(Person("John", "London"), Salary(2000))))))))
          ,Dept("Office3", "Renate", List(PU(Employee(Person("Carlos", "Spain"), Salary(3000)))
            ,PU(Employee(Person("Renate", "Almeere"), Salary(3000)))))
        )
      )
    object raise extends ->((x : Salary) => Salary(x.sal + 1000))

    assert(everywhere(raise)(c1)==onceRaised)
  }

  "Collect" should "get all the salaries" in {
    object myPoly extends Poly2 {
      implicit val intCase : Case.Aux[List[Int], Int, List[Int]] = at((acc,x) => x :: acc)
    }

    assert(c1.collect==List(1000,2000,2000,2000))
  }
}
