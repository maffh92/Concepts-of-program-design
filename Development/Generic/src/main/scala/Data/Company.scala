package Data

import Base._

/*
This file contains the representation of the Company class.
This is one of the data types defined by the paper.
 */

sealed trait Company
case class C(deps : List[Dept]) extends Company

sealed trait Dept
case class D(name : String, manager : Employee, dunits : List[DUnit]) extends Dept

sealed trait DUnit
case class PU(empl : Employee) extends DUnit
case class DU(dept : Dept) extends DUnit

sealed trait Employee
case class E(person : Person, salary : Salary) extends Employee

sealed trait Person
case class P(name : String, address : String) extends Person

sealed trait Salary
case class S(n : Int) extends  Salary

object Company {

  // Generic representation of the types.
  type DeptRep = Product[String,Product[Employee,List[DUnit]]]
  type CompanyRep = List[Dept]
  type UnitRep = Plus[Employee,Dept]
  type EmployeeRep = Product[Person,Salary]
  type PersonRep = Product[String,String]
  type SalaryRep = Int

  /*
  Isomorphisms from the Company types to each Generic representation.
  */

  implicit def isoCompany = new Iso[Company,CompanyRep]{
    def fromCompany(c : Company) : CompanyRep = {
      c match {
        case C(deps) => deps
      }
    }
    def toCompany(c : CompanyRep) : Company = {
      c match{
        case x => new C(x)
      }
    }
    def from = fromCompany
    def to   = toCompany
  }

  implicit def isoDept = new Iso[Dept,DeptRep]{
    def fromDept(d : Dept) : DeptRep = {
      d match{
        case D(n,m,us) => new Product(n, new Product(m,us))
      }
    }
    def toDept(d : DeptRep) : Dept = {
      d match{
        case Product(n,Product(m,us)) => new D(n,m,us)
      }
    }
    def from = fromDept
    def to   = toDept
  }

  implicit def isoEmployee = new Iso[Employee,EmployeeRep]{
    def fromEmployee(e : Employee) : EmployeeRep = {
      e match{
        case E(p,s) => Product(p,s)
      }
    }
    def toEmployee(e : EmployeeRep) : Employee = {
      e match{
        case Product(p,s) => new E(p,s)
      }
    }
    def from = fromEmployee
    def to   = toEmployee
  }

  implicit def isoPerson = new Iso[Person,PersonRep]{
    def fromPerson(p : Person) : PersonRep = {
      p match{
        case P(n,a) => Product(n,a)
      }
    }
    def toPerson(p : PersonRep) : Person = {
      p match{
        case Product(n,a) => new P(n,a)
      }
    }
    def from = fromPerson
    def to   = toPerson
  }

  implicit def isoSalary = new Iso[Salary,SalaryRep]{
    def fromSalary(s : Salary) : SalaryRep = {
      s match{
        case S(f) => f
      }
    }
    def toSalary(s : SalaryRep) : Salary = {
      s match{
        case f => new S(f)
      }
    }
    def from = fromSalary
    def to = toSalary
  }

  implicit def isoUnit = new Iso[DUnit,UnitRep]{
    def fromUnit(u : DUnit) : UnitRep = {
      u match{
        case PU(e) => Inl(e)
        case DU(d) => Inr(d)
      }
    }
    def toUnit[T](u : UnitRep) : DUnit = {
      u match{
        case Inl(e) => new PU(e)
        case Inr(d) => new DU(d)
      }
    }
    def from = fromUnit
    def to   = toUnit
  }
}
