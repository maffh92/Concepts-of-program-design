package Data

import Base.GenericObject._

/*
This file contains the representation of the Company class. This is one of the data types defined by the paper.
 */

sealed trait Company
case class C(deps : List[Dept]) extends Company

sealed trait Dept
case class D(name : List[Char], manager : Employee, dunits : List[DUnit]) extends Dept

sealed trait DUnit
case class PU(empl : Employee) extends DUnit
case class DU(dept : Dept) extends DUnit

sealed trait Employee
case class E(person : Person, salary : Salary) extends Employee

sealed trait Person
case class P(name : List[Char], address : List[Char]) extends Person

sealed class Salary
case class S(n : Int) extends  Salary

object Company {
  //General representation of each of the different types
  type Name = List[Char]
  type Manager = Employee
  type Adress = List[Char]
  type DeptRep[T] = Product[Name,Product[Manager,List[DUnit]]]
  type CompanyRep = List[Dept]
  type UnitRep = Plus[Employee,Dept]
  type EmployeeRep = Product[Person,Salary]
  type PersonRep = Product[Name,Adress]
  type SalaryRep = Int

  //Defined for each type a different general view function.
  class GenericCompany[G[_]](implicit gl: GenericList[G],g: Generic[G]) {
    def company : G[Company] = g.view(isoCompany,() => gl.list(depth))
    def depth : G[Dept] = g.view(isoDept,() => g.product(gl.list(g.char),g.product(employee,gl.list(unit1))))
    def unit1 : G[DUnit] = g.view(isoUnit, () => g.plus(employee,depth))
    def employee : G[Employee] = g.view(isoEmployee,() => g.product(person,salary))
    def person : G[Person] = g.view(isoPerson,() => g.product(gl.list(g.char),gl.list(g.char)))
    def salary : G[Salary] = g.view(isoSalary,() => g.int)
  }

  /*
  The below function are used to defined for each of the type an isomorphic function.
  */
  def isoCompany = new Iso[Company,CompanyRep]{
    override def from = fromCompany
    override def to = toCompany
  }
  def fromCompany(c : Company) : CompanyRep = {
    c match{
      case C(deps) => deps
    }
  }
  def toCompany(c : CompanyRep) : Company = {
    c match{
      case x => new C(x)
    }
  }

  def isoDept[T] = new Iso[Dept,DeptRep[T]]{
    override def from = fromDept
    override def to = toDept
  }
  def fromDept[T](d : Dept) : DeptRep[T] = {
    d match{
      case D(n,m,us) => new Product(n, new Product(m,us))
    }
  }
  def toDept[T](d : DeptRep[T]) : Dept = {
    d match{
      case Product(n,Product(m,us)) => new D(n,m,us)
    }
  }

  def isoEmployee = new Iso[Employee,EmployeeRep]{
    override def from = fromEmployee
    override def to = toEmployee
  }
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


  def isoPerson = new Iso[Person,PersonRep]{
    override def from = fromPerson
    override def to = toPerson
  }
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

  def isoSalary = new Iso[Salary,SalaryRep]{
    override def from = fromSalary
    override def to = toSalary
  }
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

  def isoUnit = new Iso[DUnit,UnitRep]{
    override def from = fromUnit
    override def to = toUnit
  }
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
}
