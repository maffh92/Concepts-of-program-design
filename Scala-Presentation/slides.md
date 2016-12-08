---
author: Carlos Tomé Cortiñas, Renate Eilers and Matthew Swart
title: Scala
subtitle: My title here
theme: uucs
mainfont: Ubuntu Light
sansfont: Ubuntu Light
---

# Introduction

* Background information
* Scala
* Conclusion

---

# History

* 2001
* École Polytechnique Fédérale de Lausanne (EPFL) by Martin Odersky

---

# Companies (Not sure)

* Twitter
* Linkedin
* The Guardian
* FourSquare
* Sony
* etc.

---

# Projects

* PlayFramework
* Akka

---

# Scala (Still need to add a picture)

* Object oriented
* Functional
* 

---

# REPL


![](img/Repl-Example.png "Alt caption"){ width=80% }

---

# Mutable and immutable

![](img/MutableImmutable.png "Alt caption"){ width=80% }

---

# For loops

```scala
object ForLoops {
  def forEach(values : List[String]) : Unit = {
    for(value <- values){
      println(value)
    }
  }
  def rangeLoop(): Unit ={
    for( i <- 0 to 10){
        print(i)
    }
  }
  def rangeUntilLoop(): Unit ={
    for( i <- 0 until 10){
      print(i)
    }
  }
}
```

---

# For loops

```scala
object ForLoops {
  def filterLoop(values : List[Int]): Unit ={
    for( value <- values if (value % 2 == 0)){
      print(value)
    }
  }
  def concatList(values : List[List[Any]]) : List[Any] = {
    for(value <- values; n <- value) yield n
  }

}
```


---


# Function types

A => B is an abbrevication for the class scala.Function1[A,B]
package scala
```scala
trait Function1[A,B]{
	def apply(x : A) : B
}
```
```scala traits Function2...Function22```

---

# Anonymous function

```scala
scala> values.map(x => x + 1)
res3: List[Int] = List(2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
```
---

# Anonymous function

Anonymous function
(x : Int) => x + 1

This will be expanded to  (Same syntax as Java)
```scala
new Function1[Int,Int]{
	def apply(x : Int) : Int = x + 1
}
```

---

# Hierarchy


![](img/classhierarchy.png "Alt caption"){ width=80% }

---

# Apply method


---

# Classes

```scala
class Dog(name : String){
  def sound : Unit = println("Woof!")
  def movement : String = "Walk"
}
```

```java
class Dog{
	private String name;
	public Dog(String name){
		this.name = name;
	}
	public void sound(){
		System.out.println("Woof!")
	}
	public String movement(){
 		return("Walk")
	}
}
```
---

# Abstract classes

Examples

---


# Anonymous class


---

# Case Classes

```scala
case class Person(name : String) extends Animal{
  def noise = "I am a person"
}
case class Tiger() extends Animal{
  override def noise : String = "Grr"
}

case class Frog() extends Animal{
  def noise : String = "CROAK"
}
```
---

# Pattern Match

```scala
object AnimalNoise{
  def mkSound(animal : Animal) : Unit =
    animal match {
      case Frog() => println(Frog().noise)
      case Person(name) => println(name)
      case x => println(x.noise)
    }
}
```
---

# Pattern Match

```scala
object pattern{
  def all(allTypes : Any) : Unit = {
    allTypes match{
      case (x,1) => println("(x,1)")
      case (x,y) => println("(x,y)")
      case x : String => println(x)
      case Tiger() => println(Tiger().noise)
      case 1 => println("One")
      case true => print("True")
    }
  }
}
```
---



# Object

Examples.

---

# Mixin

Traits.

---

# Traits

example

---

# Mixin - Example

example

---

# Sealed

example

---

# Classes

|              | Class | Abstract class | object | object | trait |
|:------------:|:-----:|:--------------:|:------:|:------:|:-----:|
| Inherentence |       |                |        |        |       |
|  Cmposition  |       |                |        |        |       |
|  Parameters  |       |                |        |        |       |
|              |       |                |        |        |       |
|              |       |                |        |        |       |


# Tuples

---

# Evaluation
```scala
class LazyMethod(values : () => List[List[String]]) {
    def heavyComputation : List[String] = {
      for(value <- values; n <- value)
        for(value1 <- values; n1 <- value)
          for(value2 <- values; n2 <- value)
            for(value3 <- values; n3 <- value)
              for(value4 <- values; n4 <- value)
    }
}
```
...

# Currying


---

# Currying - Anonymous function
```scala
var f = (x : Int) => (y : Int) => x + 1
f: Int => (Int => Int) = $$Lambda$1113/551797833@2c58dcb1

scala> f(1)
res0: Int => Int = $$Lambda$1130/1813375175@56380231

scala> f(1)(2)
res1: Int = 2
```
---

# Typeclasses

example

---

# Typeclasses Scala

example

---

# Generics

example

---

# Generics
```scala
class GParent
class Parent extends GParent
class Child extends Parent
class ChildOfChild extends Child
case class Children[A <: Child](value : A)
case class Children1(Value : Child)
case class Ancestors[A >: Child](value : A)
case class Family[P >: Parent, C <: Child]
		(ancestors : (P,P), children : List[C])
```
...


# Covariance

example

---

# Questions

Eend


<!-- Local Variables:  -->
<!-- pandoc/write: beamer -->
<!-- pandoc/latex-engine: "xelatex" -->
<!-- pandoc/template: "beamer-template.tex" -->
<!-- End:  -->
