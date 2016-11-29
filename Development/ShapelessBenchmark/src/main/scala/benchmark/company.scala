package benchmark

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

sealed trait Company
case class C(deps : List[Departament]) extends Company

sealed trait Departament
case class Dept(name : String, manager : String, dunits : List[DUnit])

sealed trait DUnit
case class PU(empl : Employee) extends DUnit
case class DU(dept : Departament) extends DUnit

sealed trait Employee
case class E(person : Person, salary : Float) extends Employee

sealed trait Person
case class P(name : String, address : String)

