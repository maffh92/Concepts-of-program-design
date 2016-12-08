/**
  * Created by maffh on 12/4/16.
  */




class Box[+A]

class Box2[-A]

object DoSomething{
  def foo(v : Box[Parent]){}
  def bar(v : Box2[Parent]){}
}
class GParent
class Parent extends GParent
class Child extends Parent
class ChildOfChild extends Child
case class Children[A <: Child](value : A)
case class Children1(Value : Child)
case class Ancestors[A >: Child](value : A)
case class Family[P >: Parent, C <: Child](ancestors : (P,P), children : List[C])

class RandomClass{
  def doRandom(s: Any) : String = {
    s match {
      case  () => ""
    }
  }
}