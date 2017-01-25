package types

/*
 * data Company = C [Dept]
 * data Dept = D Name Manager [DUnit]
 * data DUnit = PU Employee | DU Dept
 * data Employee = E Person Salary
 * data Person = P Name Address
 * data Salary = S Float
 * type Manager = Employee
 * type Name = String
 * type Address = String
 */

case class Company(deps : List[Dept])

case class Dept(name : String, manager : String, dunits : List[DUnit])

sealed trait DUnit
case class PU(empl : Employee)    extends DUnit
case class DU(dept : Dept) extends DUnit

case class Employee(person : Person, salary : Salary)
case class Person(name : String, address : String)

case class Salary(sal : Int)