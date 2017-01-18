class GParent
class Parent extends GParent
class Child extends Parent
class ChildOfChild extends Child
case class Children[A <: Child](value : A)
case class Children1(Value : Child)
case class Ancestors[A >: Child](value : A)
case class Family[P >: Parent, C <: Parent](ancestors : (P,P), children : List[C])


def BigFunc(s1 : Int)(s2 : Int)(s3 : Int)(s4 : Int)(s5 : Int)(s6 : Int)(s7 : Int)(s8 : Int)(s9 : Int)(s10 : Int)(s11 : Int)(s12 : Int)(s13 : Int)(s14 : Int)(s15 : Int)(s16 : Int)(s17 : Int)(s18 : Int)(s19 : Int)(s20 : Int)(s21 : Int)(s22 : Int)(s23 : Int)(s24 : Int)(s25 : Int) : Int = {
		s2
}